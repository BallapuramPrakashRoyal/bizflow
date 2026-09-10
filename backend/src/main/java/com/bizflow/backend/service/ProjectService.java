package com.bizflow.backend.service;

import com.bizflow.backend.dto.ProjectRequest;
import com.bizflow.backend.dto.ProjectResponse;
import com.bizflow.backend.dto.ProjectUpdateRequest;
import com.bizflow.backend.entity.OrganizationMember;
import com.bizflow.backend.entity.Project;
import com.bizflow.backend.exception.ForbiddenException;
import com.bizflow.backend.exception.ResourceNotFoundException;
import com.bizflow.backend.repository.OrganizationMemberRepository;
import com.bizflow.backend.repository.ProjectRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final AuditLogService auditLogService;

    public ProjectService(
            ProjectRepository projectRepository,
            OrganizationMemberRepository organizationMemberRepository,
            AuditLogService auditLogService) {

        this.projectRepository = projectRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public ProjectResponse createProject(
            ProjectRequest request,
            Long currentUserId) {

        organizationMemberRepository
                .findByOrganizationIdAndUserId(
                        request.getOrganizationId(),
                        currentUserId)
                .orElseThrow(() ->
                        new ForbiddenException(
                                "You are not a member of this organization"));

        Project project = new Project(
                request.getName(),
                request.getDescription(),
                currentUserId,
                request.getOrganizationId()
        );

        Project savedProject = projectRepository.save(project);

        auditLogService.createLog(
                currentUserId,
                "PROJECT_CREATED",
                "PROJECT",
                savedProject.getId()
        );

        return toResponse(savedProject);
    }

    public List<ProjectResponse> getAllProjects(Long currentUserId) {

        List<OrganizationMember> memberships =
                organizationMemberRepository.findByUserId(currentUserId);

        List<Long> organizationIds = memberships.stream()
                .map(OrganizationMember::getOrganizationId)
                .toList();

        return projectRepository.findAll()
                .stream()
                .filter(project ->
                        organizationIds.contains(project.getOrganizationId()))
                .map(this::toResponse)
                .toList();
    }

    public ProjectResponse getProjectById(
            Long id,
            Long currentUserId) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"));

        checkOrganizationAccess(project, currentUserId);

        return toResponse(project);
    }

    @Transactional
    public ProjectResponse updateProject(
            Long id,
            ProjectUpdateRequest request,
            Long currentUserId) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"));

        checkOrganizationAccess(project, currentUserId);

        if (!project.getOwnerId().equals(currentUserId)) {
            throw new ForbiddenException(
                    "You are not allowed to update this project");
        }

        project.setName(request.getName());
        project.setDescription(request.getDescription());

        Project updatedProject = projectRepository.save(project);

        auditLogService.createLog(
                currentUserId,
                "PROJECT_UPDATED",
                "PROJECT",
                updatedProject.getId()
        );

        return toResponse(updatedProject);
    }

    @Transactional
    public void deleteProject(
            Long id,
            Long currentUserId) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"));

        checkOrganizationAccess(project, currentUserId);

        if (!project.getOwnerId().equals(currentUserId)) {
            throw new ForbiddenException(
                    "You are not allowed to delete this project");
        }

        projectRepository.delete(project);

        auditLogService.createLog(
                currentUserId,
                "PROJECT_DELETED",
                "PROJECT",
                project.getId()
        );
    }

    private void checkOrganizationAccess(
            Project project,
            Long currentUserId) {

        boolean isMember =
                organizationMemberRepository
                        .existsByOrganizationIdAndUserId(
                                project.getOrganizationId(),
                                currentUserId);

        if (!isMember) {
            throw new ForbiddenException(
                    "You are not a member of this project's organization");
        }
    }

    private ProjectResponse toResponse(Project project) {

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getOwnerId(),
                project.getOrganizationId(),
                project.getCreatedAt()
        );
    }
}