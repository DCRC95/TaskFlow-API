package com.taskflow.api.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.api.dto.CreateProjectRequest;
import com.taskflow.api.dto.ProjectResponse;
import com.taskflow.api.dto.UpdateProjectRequest;
import com.taskflow.api.service.ProjectService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/projects")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final ProjectService projects;

    public ProjectController(ProjectService projects) {
        this.projects = projects;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody CreateProjectRequest req) {
        ProjectResponse created = projects.create(req);
        return ResponseEntity
                .created(URI.create("/api/v1/projects/" + created.id()))
                .body(created);
    }

    @GetMapping
    public List<ProjectResponse> list() {
        return projects.list();
    }

    @GetMapping("/{id}")
    public ProjectResponse get(@PathVariable long id) {
        return projects.get(id);
    }

    @PatchMapping("/{id}")
    public ProjectResponse update(@PathVariable long id, @Valid @RequestBody UpdateProjectRequest req) {
        return projects.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        projects.delete(id);
        return ResponseEntity.noContent().build();
    }
}
