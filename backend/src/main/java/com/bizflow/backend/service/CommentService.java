package com.bizflow.backend.service;

import com.bizflow.backend.dto.CommentRequest;
import com.bizflow.backend.dto.CommentResponse;
import com.bizflow.backend.entity.Comment;
import com.bizflow.backend.entity.Task;
import com.bizflow.backend.exception.ForbiddenException;
import com.bizflow.backend.exception.ResourceNotFoundException;
import com.bizflow.backend.repository.CommentRepository;
import com.bizflow.backend.repository.TaskRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final AuditLogService auditLogService;

    public CommentService(
            CommentRepository commentRepository,
            TaskRepository taskRepository,
            AuditLogService auditLogService) {

        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.auditLogService = auditLogService;
    }

    public CommentResponse createComment(
            CommentRequest request,
            Long currentUserId) {

        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found"));

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

    public List<CommentResponse> getCommentsByTaskId(Long taskId) {

        taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found"));

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