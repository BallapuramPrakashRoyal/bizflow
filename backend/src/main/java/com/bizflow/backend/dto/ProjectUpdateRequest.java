package com.bizflow.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class ProjectUpdateRequest {

    @NotBlank(message = "Project name is required")
    private String name;

    private String description;

    public ProjectUpdateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}