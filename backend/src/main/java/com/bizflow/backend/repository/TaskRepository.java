package com.bizflow.backend.repository;

import com.bizflow.backend.entity.Task;
import com.bizflow.backend.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    List<Task> findByProjectIdIn(List<Long> projectIds);

    List<Task> findByProjectIdInAndAssignedUserId(
            List<Long> projectIds,
            Long assignedUserId);

    List<Task> findByProjectIdInAndStatus(
            List<Long> projectIds,
            TaskStatus status);
}