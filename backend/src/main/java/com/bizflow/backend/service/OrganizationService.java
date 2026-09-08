package com.bizflow.backend.service;

import com.bizflow.backend.dto.OrganizationRequest;
import com.bizflow.backend.dto.OrganizationResponse;
import com.bizflow.backend.dto.OrganizationRoleUpdateRequest;
import com.bizflow.backend.entity.Organization;
import com.bizflow.backend.entity.OrganizationMember;
import com.bizflow.backend.entity.Role;
import com.bizflow.backend.repository.OrganizationMemberRepository;
import com.bizflow.backend.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import com.bizflow.backend.entity.OrganizationMember;
import com.bizflow.backend.repository.OrganizationMemberRepository;

import java.util.List;

import com.bizflow.backend.dto.OrganizationMemberRequest;
import com.bizflow.backend.entity.User;
import com.bizflow.backend.repository.UserRepository;
import com.bizflow.backend.exception.ForbiddenException;
import com.bizflow.backend.exception.ResourceNotFoundException;

import com.bizflow.backend.dto.OrganizationRoleUpdateRequest;
@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final UserRepository userRepository;

    public OrganizationService(
        OrganizationRepository organizationRepository,
        OrganizationMemberRepository organizationMemberRepository,
        UserRepository userRepository) {

    this.organizationRepository = organizationRepository;
    this.organizationMemberRepository = organizationMemberRepository;
    this.userRepository = userRepository;
}

    public OrganizationResponse createOrganization(
            OrganizationRequest request,
            Long currentUserId) {

        Organization organization =
                new Organization(request.getName());

        Organization savedOrganization =
                organizationRepository.save(organization);

        OrganizationMember adminMember =
                new OrganizationMember(
                        savedOrganization.getId(),
                        currentUserId,
                        Role.ADMIN
                );

        organizationMemberRepository.save(adminMember);

        return toResponse(savedOrganization);
    }

    public OrganizationMember addMember(
        Long organizationId,
        OrganizationMemberRequest request,
        Long currentUserId) {

    // Check organization exists
    organizationRepository.findById(organizationId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Organization not found"));

    // Check current user's role
    OrganizationMember currentMember =
            organizationMemberRepository
                    .findByOrganizationIdAndUserId(
                            organizationId,
                            currentUserId)
                    .orElseThrow(() ->
                            new ForbiddenException(
                                    "You are not a member of this organization"));

    // Only ADMIN can add members
    if (currentMember.getRole() != Role.ADMIN) {
        throw new ForbiddenException(
                "Only ADMIN can add organization members");
    }

    // Check target user exists
    User user = userRepository.findById(request.getUserId())
            .orElseThrow(() ->
                    new ResourceNotFoundException("User not found"));

    // Prevent duplicate membership
    if (organizationMemberRepository
            .existsByOrganizationIdAndUserId(
                    organizationId,
                    user.getId())) {

        throw new ForbiddenException(
                "User is already a member of this organization");
    }

    // Create membership
    OrganizationMember member = new OrganizationMember(
            organizationId,
            user.getId(),
            request.getRole());

    return organizationMemberRepository.save(member);
}

    private OrganizationResponse toResponse(
            Organization organization) {

        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getCreatedAt()
        );
    }

    public List<OrganizationMember> getOrganizationMembers(Long organizationId) {

    organizationRepository.findById(organizationId)
            .orElseThrow(() ->
                    new RuntimeException("Organization not found"));

    return organizationMemberRepository.findByOrganizationId(
            organizationId
    );
}

public OrganizationMember updateMemberRole(
        Long organizationId,
        Long targetUserId,
        OrganizationRoleUpdateRequest request,
        Long currentUserId) {

    // Check organization exists
    organizationRepository.findById(organizationId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Organization not found"));

    // Find the current user's membership
    OrganizationMember currentMember =
            organizationMemberRepository
                    .findByOrganizationIdAndUserId(
                            organizationId,
                            currentUserId)
                    .orElseThrow(() ->
                            new ForbiddenException(
                                    "You are not a member of this organization"));

    // Only ADMIN can change roles
    if (currentMember.getRole() != Role.ADMIN) {
        throw new ForbiddenException(
                "Only ADMIN can update member roles");
    }

    // Find the target member
    OrganizationMember targetMember =
            organizationMemberRepository
                    .findByOrganizationIdAndUserId(
                            organizationId,
                            targetUserId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Organization member not found"));

    // Update the role
    targetMember.setRole(request.getRole());

    return organizationMemberRepository.save(targetMember);
}
}