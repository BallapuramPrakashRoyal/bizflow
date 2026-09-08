package com.bizflow.backend.controller;

import com.bizflow.backend.dto.ProjectRequest;
import com.bizflow.backend.dto.ProjectResponse;
import com.bizflow.backend.dto.ProjectUpdateRequest;
import com.bizflow.backend.entity.User;
import com.bizflow.backend.repository.UserRepository;
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
    private final UserRepository userRepository;

    public ProjectController(
            ProjectService projectService,
            UserRepository userRepository) {

        this.projectService = projectService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return projectService.createProject(
                request,
                user.getId()
        );
    }

    @GetMapping
    public List<ProjectResponse> getAllProjects(
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return projectService.getAllProjects(
                user.getId()
        );
    }

    @GetMapping("/{id}")
    public ProjectResponse getProjectById(
            @PathVariable Long id,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return projectService.getProjectById(
                id,
                user.getId()
        );
    }

    @PutMapping("/{id}")
    public ProjectResponse updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectUpdateRequest request,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return projectService.updateProject(
                id,
                request,
                user.getId()
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(
            @PathVariable Long id,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        projectService.deleteProject(
                id,
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