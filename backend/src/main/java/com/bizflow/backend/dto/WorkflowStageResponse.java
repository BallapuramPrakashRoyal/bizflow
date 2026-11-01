package com.bizflow.backend.dto;

import com.bizflow.backend.entity.WorkflowStageStatus;

import java.time.LocalDateTime;

public class WorkflowStageResponse {

    private Long id;
    private Long projectId;
    private Integer sequenceNumber;
    private String name;
    private String description;
    private String ownerRole;
    private String techStack;
    private WorkflowStageStatus status;
    private LocalDateTime updatedAt;

    public WorkflowStageResponse(
            Long id,
            Long projectId,
            Integer sequenceNumber,
            String name,
            String description,
            String ownerRole,
            String techStack,
            WorkflowStageStatus status,
            LocalDateTime updatedAt) {
        this.id = id;
        this.projectId = projectId;
        this.sequenceNumber = sequenceNumber;
        this.name = name;
        this.description = description;
        this.ownerRole = ownerRole;
        this.techStack = techStack;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public Long getProjectId() { return projectId; }
    public Integer getSequenceNumber() { return sequenceNumber; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getOwnerRole() { return ownerRole; }
    public String getTechStack() { return techStack; }
    public WorkflowStageStatus getStatus() { return status; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
