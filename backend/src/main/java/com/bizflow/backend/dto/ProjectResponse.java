package com.bizflow.backend.dto;

import java.time.LocalDateTime;

public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Long organizationId;
    private LocalDateTime createdAt;

    public ProjectResponse(
            Long id,
            String name,
            String description,
            Long ownerId,
            Long organizationId,
            LocalDateTime createdAt) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.organizationId = organizationId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}