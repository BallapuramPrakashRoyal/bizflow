package com.bizflow.backend.dto;

import com.bizflow.backend.entity.Role;
import jakarta.validation.constraints.NotNull;

public class OrganizationRoleUpdateRequest {

    @NotNull(message = "Role is required")
    private Role role;

    public OrganizationRoleUpdateRequest() {
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}