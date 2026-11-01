package com.bizflow.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class WorkflowStageRequest {

    @NotBlank(message = "Stage name is required")
    private String name;

    private String description;

    @NotBlank(message = "Stage owner role is required")
    private String ownerRole;

    private String techStack;

    public WorkflowStageRequest() {
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOwnerRole() { return ownerRole; }
    public void setOwnerRole(String ownerRole) { this.ownerRole = ownerRole; }
    public String getTechStack() { return techStack; }
    public void setTechStack(String techStack) { this.techStack = techStack; }
}
