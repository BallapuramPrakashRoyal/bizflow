package com.bizflow.backend.controller;

import com.bizflow.backend.entity.AuditLog;
import com.bizflow.backend.entity.User;
import com.bizflow.backend.repository.UserRepository;
import com.bizflow.backend.service.AuditLogService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    public AuditLogController(
            AuditLogService auditLogService,
            UserRepository userRepository) {

        this.auditLogService = auditLogService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public List<AuditLog> getMyLogs(
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return auditLogService.getLogsByUser(user.getId());
    }

    @GetMapping
    public List<AuditLog> getLogsByEntity(
            @RequestParam String entityType,
            @RequestParam Long entityId,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return auditLogService.getLogsByEntity(
                entityType,
                entityId,
                user.getId()
        );
    }

    private User getAuthenticatedUser(
            Authentication authentication) {

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }
}