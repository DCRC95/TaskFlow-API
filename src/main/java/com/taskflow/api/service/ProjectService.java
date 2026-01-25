package com.taskflow.api.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.taskflow.api.domain.Project;
import com.taskflow.api.domain.User;
import com.taskflow.api.dto.CreateProjectRequest;
import com.taskflow.api.dto.ProjectResponse;
import com.taskflow.api.dto.UpdateProjectRequest;
import com.taskflow.api.exception.NotFoundException;
import com.taskflow.api.repository.ProjectRepository;
import com.taskflow.api.repository.UserRepository;
import com.taskflow.api.security.CurrentUser;

@Service
public class ProjectService {

    private final ProjectRepository projects;
    private final UserRepository users;
    private final CurrentUser currentUser;

    public ProjectService(ProjectRepository projects, UserRepository users, CurrentUser currentUser) {
        this.projects = projects;
        this.users = users;
        this.currentUser = currentUser;
    }

    public ProjectResponse create(CreateProjectRequest req) {
        String email = currentUser.email();
        User owner = users.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Instant now = Instant.now();
        Project p = new Project(owner, req.name(), req.description(), now, now);
        return toResponse(projects.save(p));
    }

    public List<ProjectResponse> list() {
        String email = currentUser.email();
        return projects.findByOwnerEmail(email).stream()
                .map(this::toResponse)
                .toList();
    }

    public ProjectResponse get(long id) {
        String email = currentUser.email();
        Project p = projects.findByIdAndOwnerEmail(id, email)
                .orElseThrow(() -> new NotFoundException("Project not found"));
        return toResponse(p);
    }

    public ProjectResponse update(long id, UpdateProjectRequest req) {
        String email = currentUser.email();
        Project p = projects.findByIdAndOwnerEmail(id, email)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        if (req.name() != null) p.setName(req.name());
        if (req.description() != null) p.setDescription(req.description());
        p.setUpdatedAt(Instant.now());

        return toResponse(projects.save(p));
    }

    public void delete(long id) {
        String email = currentUser.email();
        if (!projects.existsByIdAndOwnerEmail(id, email)) {
            throw new NotFoundException("Project not found");
        }
        projects.deleteById(id);
    }

    private ProjectResponse toResponse(Project p) {
        return new ProjectResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
