package com.bizflow.backend.service;

import com.bizflow.backend.dto.ProjectRequest;
import com.bizflow.backend.dto.ProjectResponse;
import com.bizflow.backend.entity.Project;
import com.bizflow.backend.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import com.bizflow.backend.exception.ResourceNotFoundException;

import com.bizflow.backend.dto.ProjectUpdateRequest;
import com.bizflow.backend.exception.ResourceNotFoundException;
import com.bizflow.backend.exception.ForbiddenException;
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public ProjectResponse createProject(
            ProjectRequest request,
            Long ownerId) {

        Project project = new Project(
                request.getName(),
                request.getDescription(),
                ownerId
        );

        Project savedProject = projectRepository.save(project);

        return new ProjectResponse(
                savedProject.getId(),
                savedProject.getName(),
                savedProject.getDescription(),
                savedProject.getOwnerId(),
                savedProject.getCreatedAt()
        );
    }
    public List<ProjectResponse> getAllProjects() {

    return projectRepository.findAll()
            .stream()
            .map(project -> new ProjectResponse(
                    project.getId(),
                    project.getName(),
                    project.getDescription(),
                    project.getOwnerId(),
                    project.getCreatedAt()
            ))
            .toList();
}

public ProjectResponse getProjectById(Long id) {

    Project project = projectRepository.findById(id)
            .orElseThrow(() ->
                 new ResourceNotFoundException("Project not found"));

    return new ProjectResponse(
            project.getId(),
            project.getName(),
            project.getDescription(),
            project.getOwnerId(),
            project.getCreatedAt()
    );
}

public ProjectResponse updateProject(
        Long projectId,
        ProjectUpdateRequest request,
        Long currentUserId) {

    Project project = projectRepository.findById(projectId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Project not found"));

    if (!project.getOwnerId().equals(currentUserId)) {
    throw new ForbiddenException(
            "You are not allowed to update this project");
}

    project.setName(request.getName());
    project.setDescription(request.getDescription());

    Project updatedProject = projectRepository.save(project);

    return new ProjectResponse(
            updatedProject.getId(),
            updatedProject.getName(),
            updatedProject.getDescription(),
            updatedProject.getOwnerId(),
            updatedProject.getCreatedAt()
    );
}
}