package com.bizflow.backend.service;

import com.bizflow.backend.dto.CommentRequest;
import com.bizflow.backend.dto.CommentResponse;
import com.bizflow.backend.entity.Comment;
import com.bizflow.backend.entity.Project;
import com.bizflow.backend.entity.Task;
import com.bizflow.backend.exception.ForbiddenException;
import com.bizflow.backend.exception.ResourceNotFoundException;
import com.bizflow.backend.repository.CommentRepository;
import com.bizflow.backend.repository.OrganizationMemberRepository;
import com.bizflow.backend.repository.ProjectRepository;
import com.bizflow.backend.repository.TaskRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final AuditLogService auditLogService;

    public CommentService(
            CommentRepository commentRepository,
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            OrganizationMemberRepository organizationMemberRepository,
            AuditLogService auditLogService) {

        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public CommentResponse createComment(
            CommentRequest request,
            Long currentUserId) {

        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found"));

        Project project = projectRepository.findById(task.getProjectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        boolean isOrganizationMember =
                organizationMemberRepository
                        .existsByOrganizationIdAndUserId(
                                project.getOrganizationId(),
                                currentUserId);

        if (!isOrganizationMember) {
            throw new ForbiddenException(
                    "You are not a member of this task's organization");
        }

        if (task.getAssignedUserId() != null
                && !task.getAssignedUserId().equals(currentUserId)) {

            throw new ForbiddenException(
                    "You are not allowed to comment on this task");
        }

        Comment comment = new Comment(
                request.getContent(),
                request.getTaskId(),
                currentUserId
        );

        Comment savedComment = commentRepository.save(comment);

        auditLogService.createLog(
                currentUserId,
                "COMMENT_CREATED",
                "COMMENT",
                savedComment.getId()
        );

        return toResponse(savedComment);
    }

    public List<CommentResponse> getCommentsByTaskId(
            Long taskId,
            Long currentUserId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found"));

        Project project = projectRepository.findById(task.getProjectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        boolean isOrganizationMember =
                organizationMemberRepository
                        .existsByOrganizationIdAndUserId(
                                project.getOrganizationId(),
                                currentUserId);

        if (!isOrganizationMember) {
            throw new ForbiddenException(
                    "You are not a member of this task's organization");
        }

        return commentRepository.findByTaskId(taskId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CommentResponse toResponse(Comment comment) {

        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getTaskId(),
                comment.getUserId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}