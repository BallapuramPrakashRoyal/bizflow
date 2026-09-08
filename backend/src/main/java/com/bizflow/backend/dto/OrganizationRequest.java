package com.bizflow.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class OrganizationRequest {

    @NotBlank(message = "Organization name is required")
    private String name;

    public OrganizationRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}