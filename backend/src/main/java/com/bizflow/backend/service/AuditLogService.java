package com.bizflow.backend.service;

import com.bizflow.backend.entity.AuditLog;
import com.bizflow.backend.entity.Comment;
import com.bizflow.backend.entity.Organization;
import com.bizflow.backend.entity.OrganizationMember;
import com.bizflow.backend.entity.Project;
import com.bizflow.backend.entity.Task;
import com.bizflow.backend.exception.ForbiddenException;
import com.bizflow.backend.exception.ResourceNotFoundException;
import com.bizflow.backend.repository.AuditLogRepository;
import com.bizflow.backend.repository.CommentRepository;
import com.bizflow.backend.repository.OrganizationMemberRepository;
import com.bizflow.backend.repository.OrganizationRepository;
import com.bizflow.backend.repository.ProjectRepository;
import com.bizflow.backend.repository.TaskRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final CommentRepository commentRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    public AuditLogService(
            AuditLogRepository auditLogRepository,
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            CommentRepository commentRepository,
            OrganizationRepository organizationRepository,
            OrganizationMemberRepository organizationMemberRepository) {

        this.auditLogRepository = auditLogRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.commentRepository = commentRepository;
        this.organizationRepository = organizationRepository;
        this.organizationMemberRepository = organizationMemberRepository;
    }

    public AuditLog createLog(
            Long userId,
            String action,
            String entityType,
            Long entityId) {

        AuditLog auditLog = new AuditLog(
                userId,
                action,
                entityType,
                entityId
        );

        return auditLogRepository.save(auditLog);
    }

    public List<AuditLog> getLogsByUser(Long userId) {

        return auditLogRepository.findByUserId(userId);
    }

    public List<AuditLog> getLogsByEntity(
            String entityType,
            Long entityId,
            Long currentUserId) {

        if ("TASK".equalsIgnoreCase(entityType)) {

            Task task = taskRepository.findById(entityId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Task not found"));

            Project project = projectRepository.findById(task.getProjectId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Project not found"));

            checkOrganizationAccess(
                    project.getOrganizationId(),
                    currentUserId
            );
        }

        else if ("PROJECT".equalsIgnoreCase(entityType)) {

            Project project = projectRepository.findById(entityId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Project not found"));

            checkOrganizationAccess(
                    project.getOrganizationId(),
                    currentUserId
            );
        }

        else if ("COMMENT".equalsIgnoreCase(entityType)) {

            Comment comment = commentRepository.findById(entityId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Comment not found"));

            Task task = taskRepository.findById(comment.getTaskId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Task not found"));

            Project project = projectRepository.findById(task.getProjectId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Project not found"));

            checkOrganizationAccess(
                    project.getOrganizationId(),
                    currentUserId
            );
        }

        else if ("ORGANIZATION".equalsIgnoreCase(entityType)) {

            organizationRepository.findById(entityId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Organization not found"));

            checkOrganizationAccess(
                    entityId,
                    currentUserId
            );
        }

        else if ("ORGANIZATION_MEMBER".equalsIgnoreCase(entityType)) {

            OrganizationMember member =
                    organizationMemberRepository.findById(entityId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Organization member not found"));

            checkOrganizationAccess(
                    member.getOrganizationId(),
                    currentUserId
            );
        }

        else {

            throw new ForbiddenException(
                    "Audit history is not supported for this entity type");
        }

        return auditLogRepository.findByEntityTypeAndEntityId(
                entityType,
                entityId
        );
    }

    private void checkOrganizationAccess(
            Long organizationId,
            Long currentUserId) {

        boolean isMember =
                organizationMemberRepository
                        .existsByOrganizationIdAndUserId(
                                organizationId,
                                currentUserId
                        );

        if (!isMember) {
            throw new ForbiddenException(
                    "You are not a member of this organization");
        }
    }
}