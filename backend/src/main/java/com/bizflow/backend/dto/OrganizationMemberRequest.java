package com.bizflow.backend.dto;

import com.bizflow.backend.entity.Role;
import jakarta.validation.constraints.NotNull;

public class OrganizationMemberRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Role is required")
    private Role role;

    public OrganizationMemberRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}