package com.bizflow.backend.controller;

import com.bizflow.backend.dto.WorkflowStageRequest;
import com.bizflow.backend.dto.WorkflowStageResponse;
import com.bizflow.backend.dto.WorkflowStageStatusRequest;
import com.bizflow.backend.service.CurrentUserService;
import com.bizflow.backend.service.WorkflowService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/workflow")
public class WorkflowController {

    private final WorkflowService workflowService;
    private final CurrentUserService currentUserService;

    public WorkflowController(
            WorkflowService workflowService,
            CurrentUserService currentUserService) {
        this.workflowService = workflowService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<WorkflowStageResponse> getStages(
            @PathVariable Long projectId,
            Authentication authentication) {
        return workflowService.getStages(
                projectId,
                currentUserService.getCurrentUserId(authentication)
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<WorkflowStageResponse> createStages(
            @PathVariable Long projectId,
            @Valid @RequestBody List<WorkflowStageRequest> requests,
            Authentication authentication) {
        return workflowService.createStages(
                projectId,
                requests,
                currentUserService.getCurrentUserId(authentication)
        );
    }

    @PutMapping("/{stageId}")
    public WorkflowStageResponse updateStatus(
            @PathVariable Long projectId,
            @PathVariable Long stageId,
            @Valid @RequestBody WorkflowStageStatusRequest request,
            Authentication authentication) {
        return workflowService.updateStatus(
                projectId,
                stageId,
                request.getStatus(),
                currentUserService.getCurrentUserId(authentication)
        );
    }
}
