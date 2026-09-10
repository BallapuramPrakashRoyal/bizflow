package com.bizflow.backend.controller;

import com.bizflow.backend.dto.ProjectRequest;
import com.bizflow.backend.dto.ProjectResponse;
import com.bizflow.backend.dto.ProjectUpdateRequest;
import com.bizflow.backend.service.CurrentUserService;
import com.bizflow.backend.service.ProjectService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final CurrentUserService currentUserService;

    public ProjectController(
            ProjectService projectService,
            CurrentUserService currentUserService) {

        this.projectService = projectService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication) {

        Long currentUserId =
                currentUserService.getCurrentUserId(authentication);

        return projectService.createProject(
                request,
                currentUserId
        );
    }

    @GetMapping
    public List<ProjectResponse> getAllProjects(
            Authentication authentication) {

        Long currentUserId =
                currentUserService.getCurrentUserId(authentication);

        return projectService.getAllProjects(currentUserId);
    }

    @GetMapping("/{id}")
    public ProjectResponse getProjectById(
            @PathVariable Long id,
            Authentication authentication) {

        Long currentUserId =
                currentUserService.getCurrentUserId(authentication);

        return projectService.getProjectById(
                id,
                currentUserId
        );
    }

    @PutMapping("/{id}")
    public ProjectResponse updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectUpdateRequest request,
            Authentication authentication) {

        Long currentUserId =
                currentUserService.getCurrentUserId(authentication);

        return projectService.updateProject(
                id,
                request,
                currentUserId
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(
            @PathVariable Long id,
            Authentication authentication) {

        Long currentUserId =
                currentUserService.getCurrentUserId(authentication);

        projectService.deleteProject(
                id,
                currentUserId
        );
    }
}