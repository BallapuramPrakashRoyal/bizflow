package com.bizflow.backend.dto;

import com.bizflow.backend.entity.TaskStatus;

import java.time.LocalDateTime;

public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Long projectId;
    private Long assignedUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TaskResponse(
            Long id,
            String title,
            String description,
            TaskStatus status,
            Long projectId,
            Long assignedUserId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.projectId = projectId;
        this.assignedUserId = assignedUserId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}