package com.bizflow.backend.controller;

import com.bizflow.backend.dto.TaskRequest;
import com.bizflow.backend.dto.TaskResponse;
import com.bizflow.backend.dto.TaskUpdateRequest;
import com.bizflow.backend.entity.User;
import com.bizflow.backend.repository.UserRepository;
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
    private final UserRepository userRepository;

    public TaskController(
            TaskService taskService,
            UserRepository userRepository) {

        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @Valid @RequestBody TaskRequest request,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return taskService.createTask(
                request,
                user.getId()
        );
    }

    @GetMapping
    public List<TaskResponse> getAllTasks(
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return taskService.getAllTasks(
                user.getId()
        );
    }

    @GetMapping("/{taskId}")
    public TaskResponse getTaskById(
            @PathVariable Long taskId,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return taskService.getTaskById(
                taskId,
                user.getId()
        );
    }

    @PutMapping("/{taskId}")
    public TaskResponse updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateRequest request,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return taskService.updateTask(
                taskId,
                request,
                user.getId()
        );
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(
            @PathVariable Long taskId,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        taskService.deleteTask(
                taskId,
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