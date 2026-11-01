package com.bizflow.backend.service;

import com.bizflow.backend.dto.WorkflowStageRequest;
import com.bizflow.backend.dto.WorkflowStageResponse;
import com.bizflow.backend.entity.Project;
import com.bizflow.backend.entity.ProjectWorkflowStage;
import com.bizflow.backend.entity.WorkflowStageStatus;
import com.bizflow.backend.exception.ForbiddenException;
import com.bizflow.backend.exception.ResourceNotFoundException;
import com.bizflow.backend.repository.OrganizationMemberRepository;
import com.bizflow.backend.repository.ProjectRepository;
import com.bizflow.backend.repository.ProjectWorkflowStageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkflowService {

    private final ProjectWorkflowStageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final AuditLogService auditLogService;

    public WorkflowService(
            ProjectWorkflowStageRepository stageRepository,
            ProjectRepository projectRepository,
            OrganizationMemberRepository organizationMemberRepository,
            AuditLogService auditLogService) {
        this.stageRepository = stageRepository;
        this.projectRepository = projectRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.auditLogService = auditLogService;
    }

    public List<WorkflowStageResponse> getStages(Long projectId, Long currentUserId) {
        checkProjectAccess(projectId, currentUserId);
        return stageRepository.findByProjectIdOrderBySequenceNumberAsc(projectId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public List<WorkflowStageResponse> createStages(
            Long projectId,
            List<WorkflowStageRequest> requests,
            Long currentUserId) {
        Project project = checkProjectAccess(projectId, currentUserId);
        if (!project.getOwnerId().equals(currentUserId)) {
            throw new ForbiddenException("Only the project owner can design its workflow");
        }
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("At least one workflow stage is required");
        }

        stageRepository.deleteByProjectId(projectId);
        List<ProjectWorkflowStage> stages = new java.util.ArrayList<>();
        for (int index = 0; index < requests.size(); index++) {
            WorkflowStageRequest request = requests.get(index);
            stages.add(new ProjectWorkflowStage(
                    projectId,
                    index + 1,
                    request.getName().trim(),
                    request.getDescription(),
                    request.getOwnerRole().trim(),
                    request.getTechStack()
            ));
        }
        List<ProjectWorkflowStage> savedStages = stageRepository.saveAll(stages);
        auditLogService.createLog(currentUserId, "WORKFLOW_CREATED", "PROJECT", projectId);
        return savedStages.stream().map(this::toResponse).toList();
    }

    @Transactional
    public WorkflowStageResponse updateStatus(
            Long projectId,
            Long stageId,
            WorkflowStageStatus status,
            Long currentUserId) {
        Project project = checkProjectAccess(projectId, currentUserId);
        if (!project.getOwnerId().equals(currentUserId)) {
            throw new ForbiddenException("Only the project owner can update its workflow");
        }
        ProjectWorkflowStage stage = stageRepository.findById(stageId)
                .filter(item -> item.getProjectId().equals(projectId))
                .orElseThrow(() -> new ResourceNotFoundException("Workflow stage not found"));
        stage.setStatus(status);
        ProjectWorkflowStage updatedStage = stageRepository.save(stage);
        auditLogService.createLog(currentUserId, "WORKFLOW_STAGE_UPDATED", "PROJECT", projectId);
        return toResponse(updatedStage);
    }

    private Project checkProjectAccess(Long projectId, Long currentUserId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        boolean isMember = organizationMemberRepository.existsByOrganizationIdAndUserId(
                project.getOrganizationId(), currentUserId);
        if (!isMember) {
            throw new ForbiddenException("You are not a member of this project's organization");
        }
        return project;
    }

    private WorkflowStageResponse toResponse(ProjectWorkflowStage stage) {
        return new WorkflowStageResponse(
                stage.getId(),
                stage.getProjectId(),
                stage.getSequenceNumber(),
                stage.getName(),
                stage.getDescription(),
                stage.getOwnerRole(),
                stage.getTechStack(),
                stage.getStatus(),
                stage.getUpdatedAt()
        );
    }
}
