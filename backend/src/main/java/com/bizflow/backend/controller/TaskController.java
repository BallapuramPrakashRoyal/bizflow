package com.bizflow.backend.controller;

import com.bizflow.backend.dto.TaskRequest;
import com.bizflow.backend.dto.TaskResponse;
import com.bizflow.backend.dto.TaskUpdateRequest;
import com.bizflow.backend.service.CurrentUserService;
import com.bizflow.backend.service.TaskService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final CurrentUserService currentUserService;

    public TaskController(
            TaskService taskService,
            CurrentUserService currentUserService) {

        this.taskService = taskService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @Valid @RequestBody TaskRequest request,
            Authentication authentication) {

        Long currentUserId =
                currentUserService.getCurrentUserId(authentication);

        return taskService.createTask(
                request,
                currentUserId
        );
    }

    @GetMapping
    public List<TaskResponse> getAllTasks(
            Authentication authentication) {

        Long currentUserId =
                currentUserService.getCurrentUserId(authentication);

        return taskService.getAllTasks(currentUserId);
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(
            @PathVariable Long id,
            Authentication authentication) {

        Long currentUserId =
                currentUserService.getCurrentUserId(authentication);

        return taskService.getTaskById(
                id,
                currentUserId
        );
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest request,
            Authentication authentication) {

        Long currentUserId =
                currentUserService.getCurrentUserId(authentication);

        return taskService.updateTask(
                id,
                request,
                currentUserId
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(
            @PathVariable Long id,
            Authentication authentication) {

        Long currentUserId =
                currentUserService.getCurrentUserId(authentication);

        taskService.deleteTask(
                id,
                currentUserId
        );
    }
}