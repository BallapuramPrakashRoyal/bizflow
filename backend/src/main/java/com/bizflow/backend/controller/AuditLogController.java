package com.bizflow.backend.controller;

import com.bizflow.backend.entity.AuditLog;
import com.bizflow.backend.service.AuditLogService;
import com.bizflow.backend.service.CurrentUserService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final CurrentUserService currentUserService;

    public AuditLogController(
            AuditLogService auditLogService,
            CurrentUserService currentUserService) {

        this.auditLogService = auditLogService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/me")
    public List<AuditLog> getMyLogs(
            Authentication authentication) {

        Long currentUserId =
                currentUserService.getCurrentUserId(authentication);

        return auditLogService.getLogsByUser(currentUserId);
    }

    @GetMapping
    public List<AuditLog> getLogsByEntity(
            @RequestParam String entityType,
            @RequestParam Long entityId,
            Authentication authentication) {

        Long currentUserId =
                currentUserService.getCurrentUserId(authentication);

        return auditLogService.getLogsByEntity(
                entityType,
                entityId,
                currentUserId
        );
    }
}