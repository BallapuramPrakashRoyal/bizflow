package com.bizflow.backend.dto;

import java.time.LocalDateTime;

public class CommentResponse {

    private Long id;
    private String content;
    private Long taskId;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentResponse(
            Long id,
            String content,
            Long taskId,
            Long userId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.content = content;
        this.taskId = taskId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Long getTaskId() {
        return taskId;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}