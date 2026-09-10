package com.bizflow.backend.service;

import com.bizflow.backend.dto.OrganizationMemberRequest;
import com.bizflow.backend.dto.OrganizationRequest;
import com.bizflow.backend.dto.OrganizationResponse;
import com.bizflow.backend.dto.OrganizationRoleUpdateRequest;
import com.bizflow.backend.entity.Organization;
import com.bizflow.backend.entity.OrganizationMember;
import com.bizflow.backend.entity.Role;
import com.bizflow.backend.entity.User;
import com.bizflow.backend.exception.ForbiddenException;
import com.bizflow.backend.exception.ResourceNotFoundException;
import com.bizflow.backend.repository.OrganizationMemberRepository;
import com.bizflow.backend.repository.OrganizationRepository;
import com.bizflow.backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public OrganizationService(
            OrganizationRepository organizationRepository,
            OrganizationMemberRepository organizationMemberRepository,
            UserRepository userRepository,
            AuditLogService auditLogService) {

        this.organizationRepository = organizationRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
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

        auditLogService.createLog(
                currentUserId,
                "ORGANIZATION_CREATED",
                "ORGANIZATION",
                savedOrganization.getId()
        );

        return toResponse(savedOrganization);
    }

    @Transactional
    public OrganizationMember addMember(
            Long organizationId,
            OrganizationMemberRequest request,
            Long currentUserId) {

        // Check organization exists
        organizationRepository.findById(organizationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Organization not found"));

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
                        new ResourceNotFoundException(
                                "User not found"));

        // Prevent duplicate membership
        if (organizationMemberRepository
                .existsByOrganizationIdAndUserId(
                        organizationId,
                        user.getId())) {

            throw new ForbiddenException(
                    "User is already a member of this organization");
        }

        // Create membership
        OrganizationMember member =
                new OrganizationMember(
                        organizationId,
                        user.getId(),
                        request.getRole());

        OrganizationMember savedMember =
                organizationMemberRepository.save(member);

        auditLogService.createLog(
                currentUserId,
                "MEMBER_ADDED",
                "ORGANIZATION_MEMBER",
                savedMember.getId()
        );

        return savedMember;
    }

    public List<OrganizationMember> getOrganizationMembers(
            Long organizationId) {

        organizationRepository.findById(organizationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Organization not found"));

        return organizationMemberRepository.findByOrganizationId(
                organizationId
        );
    }

    @Transactional
    public OrganizationMember updateMemberRole(
            Long organizationId,
            Long targetUserId,
            OrganizationRoleUpdateRequest request,
            Long currentUserId) {

        // Check organization exists
        organizationRepository.findById(organizationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Organization not found"));

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

        OrganizationMember updatedMember =
                organizationMemberRepository.save(targetMember);

        auditLogService.createLog(
                currentUserId,
                "ROLE_CHANGED",
                "ORGANIZATION_MEMBER",
                updatedMember.getId()
        );

        return updatedMember;
    }

    private OrganizationResponse toResponse(
            Organization organization) {

        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getCreatedAt()
        );
    }
}