package com.bizflow.backend.service;

import com.bizflow.backend.dto.TaskRequest;
import com.bizflow.backend.dto.TaskResponse;
import com.bizflow.backend.dto.TaskUpdateRequest;
import com.bizflow.backend.entity.Project;
import com.bizflow.backend.entity.Task;
import com.bizflow.backend.exception.ForbiddenException;
import com.bizflow.backend.exception.ResourceNotFoundException;
import com.bizflow.backend.repository.OrganizationMemberRepository;
import com.bizflow.backend.repository.ProjectRepository;
import com.bizflow.backend.repository.TaskRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final AuditLogService auditLogService;

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            OrganizationMemberRepository organizationMemberRepository,
            AuditLogService auditLogService) {

        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.auditLogService = auditLogService;
    }

    public TaskResponse createTask(TaskRequest request, Long currentUserId) {

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        checkOrganizationAccess(project, currentUserId);

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

        auditLogService.createLog(
                currentUserId,
                "TASK_CREATED",
                "TASK",
                savedTask.getId()
        );

        return toResponse(savedTask);
    }

    public List<TaskResponse> getAllTasks(Long currentUserId) {

        return taskRepository.findAll()
                .stream()
                .filter(task ->
                        hasProjectAccess(task.getProjectId(), currentUserId))
                .map(this::toResponse)
                .toList();
    }

    public TaskResponse getTaskById(
            Long taskId,
            Long currentUserId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found"));

        Project project = projectRepository.findById(task.getProjectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        checkOrganizationAccess(project, currentUserId);

        return toResponse(task);
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

        checkOrganizationAccess(project, currentUserId);

        if (!project.getOwnerId().equals(currentUserId)) {
            throw new ForbiddenException(
                    "You are not allowed to update this task");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setAssignedUserId(request.getAssignedUserId());
        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(task);

        auditLogService.createLog(
                currentUserId,
                "TASK_UPDATED",
                "TASK",
                updatedTask.getId()
        );

        return toResponse(updatedTask);
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

        checkOrganizationAccess(project, currentUserId);

        if (!project.getOwnerId().equals(currentUserId)) {
            throw new ForbiddenException(
                    "You are not allowed to delete this task");
        }

        Long deletedTaskId = task.getId();

        taskRepository.delete(task);

        auditLogService.createLog(
                currentUserId,
                "TASK_DELETED",
                "TASK",
                deletedTaskId
        );
    }

    private void checkOrganizationAccess(
            Project project,
            Long currentUserId) {

        boolean isMember = organizationMemberRepository
                .existsByOrganizationIdAndUserId(
                        project.getOrganizationId(),
                        currentUserId
                );

        if (!isMember) {
            throw new ForbiddenException(
                    "You are not a member of this project's organization");
        }
    }

    private boolean hasProjectAccess(
            Long projectId,
            Long currentUserId) {

        Project project = projectRepository
                .findById(projectId)
                .orElse(null);

        if (project == null) {
            return false;
        }

        return organizationMemberRepository
                .existsByOrganizationIdAndUserId(
                        project.getOrganizationId(),
                        currentUserId
                );
    }

    private TaskResponse toResponse(Task task) {

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
}