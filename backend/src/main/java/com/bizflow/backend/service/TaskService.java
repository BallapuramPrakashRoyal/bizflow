package com.bizflow.backend.service;

import com.bizflow.backend.dto.TaskRequest;
import com.bizflow.backend.dto.TaskResponse;
import com.bizflow.backend.entity.Project;
import com.bizflow.backend.entity.Task;
import com.bizflow.backend.exception.ForbiddenException;
import com.bizflow.backend.exception.ResourceNotFoundException;
import com.bizflow.backend.repository.ProjectRepository;
import com.bizflow.backend.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import com.bizflow.backend.dto.TaskUpdateRequest;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository) {

        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public TaskResponse createTask(
            TaskRequest request,
            Long currentUserId) {

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        if (!project.getOwnerId().equals(currentUserId)) {
            throw new ForbiddenException(
                    "You are not allowed to create a task in this project");
        }

        Task task = new Task(
                request.getTitle(),
                request.getDescription(),
                request.getProjectId(),
                request.getAssignedUserId()
        );

        Task savedTask = taskRepository.save(task);

        return new TaskResponse(
                savedTask.getId(),
                savedTask.getTitle(),
                savedTask.getDescription(),
                savedTask.getStatus(),
                savedTask.getProjectId(),
                savedTask.getAssignedUserId(),
                savedTask.getCreatedAt(),
                savedTask.getUpdatedAt()
        );
    }
    public List<TaskResponse> getAllTasks() {

    return taskRepository.findAll()
            .stream()
            .map(task -> new TaskResponse(
                    task.getId(),
                    task.getTitle(),
                    task.getDescription(),
                    task.getStatus(),
                    task.getProjectId(),
                    task.getAssignedUserId(),
                    task.getCreatedAt(),
                    task.getUpdatedAt()
            ))
            .toList();
}

public TaskResponse getTaskById(Long taskId) {

    Task task = taskRepository.findById(taskId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Task not found"));

    return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            task.getProjectId(),
            task.getAssignedUserId(),
            task.getCreatedAt(),
            task.getUpdatedAt()
    );
}

public TaskResponse updateTask(
        Long taskId,
        TaskUpdateRequest request,
        Long currentUserId) {

    Task task = taskRepository.findById(taskId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Task not found"));

    Project project = projectRepository.findById(task.getProjectId())
            .orElseThrow(() ->
                    new ResourceNotFoundException("Project not found"));

    if (!project.getOwnerId().equals(currentUserId)) {
        throw new ForbiddenException(
                "You are not allowed to update this task");
    }

    task.setTitle(request.getTitle());
    task.setDescription(request.getDescription());
    task.setStatus(request.getStatus());
    task.setAssignedUserId(request.getAssignedUserId());
    task.setUpdatedAt(java.time.LocalDateTime.now());

    Task updatedTask = taskRepository.save(task);

    return new TaskResponse(
            updatedTask.getId(),
            updatedTask.getTitle(),
            updatedTask.getDescription(),
            updatedTask.getStatus(),
            updatedTask.getProjectId(),
            updatedTask.getAssignedUserId(),
            updatedTask.getCreatedAt(),
            updatedTask.getUpdatedAt()
    );
}

public void deleteTask(
        Long taskId,
        Long currentUserId) {

    Task task = taskRepository.findById(taskId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Task not found"));

    Project project = projectRepository.findById(task.getProjectId())
            .orElseThrow(() ->
                    new ResourceNotFoundException("Project not found"));

    if (!project.getOwnerId().equals(currentUserId)) {
        throw new ForbiddenException(
                "You are not allowed to delete this task");
    }

    taskRepository.delete(task);
}

}