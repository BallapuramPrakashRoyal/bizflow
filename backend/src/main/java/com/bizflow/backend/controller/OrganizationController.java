package com.bizflow.backend.controller;

import com.bizflow.backend.dto.OrganizationRequest;
import com.bizflow.backend.dto.OrganizationResponse;
import com.bizflow.backend.dto.OrganizationRoleUpdateRequest;
import com.bizflow.backend.entity.User;
import com.bizflow.backend.repository.UserRepository;
import com.bizflow.backend.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.bizflow.backend.entity.OrganizationMember;
import java.util.List;

import com.bizflow.backend.dto.OrganizationMemberRequest;

import com.bizflow.backend.dto.OrganizationRoleUpdateRequest;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final UserRepository userRepository;

    public OrganizationController(
            OrganizationService organizationService,
            UserRepository userRepository) {

        this.organizationService = organizationService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrganizationResponse createOrganization(
            @Valid @RequestBody OrganizationRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return organizationService.createOrganization(
                request,
                user.getId()
        );
    }

    @GetMapping("/{organizationId}/members")
public List<OrganizationMember> getOrganizationMembers(
        @PathVariable Long organizationId) {

    return organizationService.getOrganizationMembers(
            organizationId
    );
}

@PostMapping("/{organizationId}/members")
@ResponseStatus(HttpStatus.CREATED)
public OrganizationMember addMember(
        @PathVariable Long organizationId,
        @Valid @RequestBody OrganizationMemberRequest request,
        Authentication authentication) {

    String email = authentication.getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    return organizationService.addMember(
            organizationId,
            request,
            user.getId()
    );
}

@PutMapping("/{organizationId}/members/{targetUserId}/role")
public OrganizationMember updateMemberRole(
        @PathVariable Long organizationId,
        @PathVariable Long targetUserId,
        @Valid @RequestBody OrganizationRoleUpdateRequest request,
        Authentication authentication) {

    String email = authentication.getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    return organizationService.updateMemberRole(
            organizationId,
            targetUserId,
            request,
            user.getId()
    );
}

}