package com.bizflow.backend.controller;

import com.bizflow.backend.dto.OrganizationMemberRequest;
import com.bizflow.backend.dto.OrganizationRequest;
import com.bizflow.backend.dto.OrganizationResponse;
import com.bizflow.backend.dto.OrganizationRoleUpdateRequest;
import com.bizflow.backend.entity.OrganizationMember;
import com.bizflow.backend.service.CurrentUserService;
import com.bizflow.backend.service.OrganizationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final CurrentUserService currentUserService;

    public OrganizationController(
            OrganizationService organizationService,
            CurrentUserService currentUserService) {

        this.organizationService = organizationService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrganizationResponse createOrganization(
            @Valid @RequestBody OrganizationRequest request,
            Authentication authentication) {

        Long currentUserId =
                currentUserService.getCurrentUserId(authentication);

        return organizationService.createOrganization(
                request,
                currentUserId
        );
    }

    @PostMapping("/{organizationId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public OrganizationMember addMember(
            @PathVariable Long organizationId,
            @Valid @RequestBody OrganizationMemberRequest request,
            Authentication authentication) {

        Long currentUserId =
                currentUserService.getCurrentUserId(authentication);

        return organizationService.addMember(
                organizationId,
                request,
                currentUserId
        );
    }

    @GetMapping("/{organizationId}/members")
    public List<OrganizationMember> getOrganizationMembers(
            @PathVariable Long organizationId,
            Authentication authentication) {

        currentUserService.getCurrentUserId(authentication);

        return organizationService.getOrganizationMembers(
                organizationId
        );
    }

    @PutMapping("/{organizationId}/members/{userId}/role")
    public OrganizationMember updateMemberRole(
            @PathVariable Long organizationId,
            @PathVariable Long userId,
            @Valid @RequestBody OrganizationRoleUpdateRequest request,
            Authentication authentication) {

        Long currentUserId =
                currentUserService.getCurrentUserId(authentication);

        return organizationService.updateMemberRole(
                organizationId,
                userId,
                request,
                currentUserId
        );
    }
}