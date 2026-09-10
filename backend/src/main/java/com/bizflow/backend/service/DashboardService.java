package com.bizflow.backend.service;

import com.bizflow.backend.dto.DashboardResponse;
import com.bizflow.backend.entity.OrganizationMember;
import com.bizflow.backend.entity.Project;
import com.bizflow.backend.entity.Task;
import com.bizflow.backend.entity.TaskStatus;
import com.bizflow.backend.repository.OrganizationMemberRepository;
import com.bizflow.backend.repository.ProjectRepository;
import com.bizflow.backend.repository.TaskRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final OrganizationMemberRepository organizationMemberRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public DashboardService(
            OrganizationMemberRepository organizationMemberRepository,
            ProjectRepository projectRepository,
            TaskRepository taskRepository) {

        this.organizationMemberRepository = organizationMemberRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    public DashboardResponse getDashboard(Long currentUserId) {

        List<OrganizationMember> memberships =
                organizationMemberRepository.findByUserId(currentUserId);

        List<Long> organizationIds = memberships.stream()
                .map(OrganizationMember::getOrganizationId)
                .distinct()
                .toList();

        if (organizationIds.isEmpty()) {
            return new DashboardResponse(
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
            );
        }

        List<Project> projects =
                projectRepository.findByOrganizationIdIn(organizationIds);

        long totalProjects = projects.size();

        if (projects.isEmpty()) {
            return new DashboardResponse(
                    totalProjects,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
            );
        }

        List<Long> projectIds = projects.stream()
                .map(Project::getId)
                .toList();

        List<Task> tasks =
                taskRepository.findByProjectIdIn(projectIds);

        long totalTasks = tasks.size();

        long todoTasks = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.TODO)
                .count();

        long inProgressTasks = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS)
                .count();

        long inReviewTasks = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.IN_REVIEW)
                .count();

        long completedTasks = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .count();

        long cancelledTasks = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.CANCELLED)
                .count();

        long myTasks = tasks.stream()
                .filter(task ->
                        task.getAssignedUserId() != null
                                && task.getAssignedUserId().equals(currentUserId))
                .count();

        return new DashboardResponse(
                totalProjects,
                totalTasks,
                todoTasks,
                inProgressTasks,
                inReviewTasks,
                completedTasks,
                cancelledTasks,
                myTasks
        );
    }
}