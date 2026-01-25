package com.taskflow.api.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.api.dto.CreateTaskRequest;
import com.taskflow.api.dto.TaskResponse;
import com.taskflow.api.dto.UpdateTaskRequest;
import com.taskflow.api.service.TaskService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService tasks;

    public TaskController(TaskService tasks) {
        this.tasks = tasks;
    }

    @PostMapping("/api/v1/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponse> create(
            @PathVariable long projectId,
            @Valid @RequestBody CreateTaskRequest req
    ) {
        TaskResponse created = tasks.create(projectId, req);
        return ResponseEntity
                .created(URI.create("/api/v1/tasks/" + created.id()))
                .body(created);
    }

    @GetMapping("/api/v1/projects/{projectId}/tasks")
    public Page<TaskResponse> list(
            @PathVariable long projectId,
            @RequestParam(required = false) com.taskflow.api.domain.TaskStatus status,
            @RequestParam(required = false) java.time.LocalDate dueBefore,
            Pageable pageable
    ) {
        return tasks.list(projectId, status, dueBefore, pageable);
    }
    
    @PatchMapping("/api/v1/tasks/{taskId}")
    public TaskResponse update(@PathVariable long taskId, @Valid @RequestBody UpdateTaskRequest req) {
        return tasks.update(taskId, req);
    }

    @DeleteMapping("/api/v1/tasks/{taskId}")
    public ResponseEntity<Void> delete(@PathVariable long taskId) {
        tasks.delete(taskId);
        return ResponseEntity.noContent().build();
    }
}
