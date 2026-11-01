package com.bizflow.backend.repository;

import com.bizflow.backend.entity.ProjectWorkflowStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectWorkflowStageRepository extends JpaRepository<ProjectWorkflowStage, Long> {
    List<ProjectWorkflowStage> findByProjectIdOrderBySequenceNumberAsc(Long projectId);
    void deleteByProjectId(Long projectId);
}
