package com.bizflow.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Column(nullable = false)
    private Long projectId;

    private Long assignedUserId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Task() {
    }

    public Task(
            String title,
            String description,
            Long projectId,
            Long assignedUserId) {

        this.title = title;
        this.description = description;
        this.projectId = projectId;
        this.assignedUserId = assignedUserId;
        this.status = TaskStatus.TODO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() {
    return id;
}

public void setId(Long id) {
    this.id = id;
}

public String getTitle() {
    return title;
}

public void setTitle(String title) {
    this.title = title;
}

public String getDescription() {
    return description;
}

public void setDescription(String description) {
    this.description = description;
}

public TaskStatus getStatus() {
    return status;
}

public void setStatus(TaskStatus status) {
    this.status = status;
}

public Long getProjectId() {
    return projectId;
}

public void setProjectId(Long projectId) {
    this.projectId = projectId;
}

public Long getAssignedUserId() {
    return assignedUserId;
}

public void setAssignedUserId(Long assignedUserId) {
    this.assignedUserId = assignedUserId;
}

public LocalDateTime getCreatedAt() {
    return createdAt;
}

public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
}

public LocalDateTime getUpdatedAt() {
    return updatedAt;
}

public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
}
}