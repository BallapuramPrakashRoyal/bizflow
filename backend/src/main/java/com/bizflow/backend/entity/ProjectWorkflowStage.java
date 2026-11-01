package com.bizflow.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_workflow_stages")
public class ProjectWorkflowStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long projectId;

    @Column(nullable = false)
    private Integer sequenceNumber;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String ownerRole;

    @Column(length = 500)
    private String techStack;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkflowStageStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public ProjectWorkflowStage() {
    }

    public ProjectWorkflowStage(
            Long projectId,
            Integer sequenceNumber,
            String name,
            String description,
            String ownerRole,
            String techStack) {
        this.projectId = projectId;
        this.sequenceNumber = sequenceNumber;
        this.name = name;
        this.description = description;
        this.ownerRole = ownerRole;
        this.techStack = techStack;
        this.status = WorkflowStageStatus.PLANNED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getProjectId() { return projectId; }
    public Integer getSequenceNumber() { return sequenceNumber; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getOwnerRole() { return ownerRole; }
    public String getTechStack() { return techStack; }
    public WorkflowStageStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setStatus(WorkflowStageStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
}
