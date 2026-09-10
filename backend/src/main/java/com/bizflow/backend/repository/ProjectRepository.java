package com.bizflow.backend.repository;

import com.bizflow.backend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByOrganizationId(Long organizationId);

    List<Project> findByOrganizationIdIn(List<Long> organizationIds);
}