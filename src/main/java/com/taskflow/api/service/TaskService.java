package com.taskflow.api.service;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.taskflow.api.domain.Project;
import com.taskflow.api.domain.Task;
import com.taskflow.api.domain.TaskStatus;
import com.taskflow.api.dto.CreateTaskRequest;
import com.taskflow.api.dto.TaskResponse;
import com.taskflow.api.dto.UpdateTaskRequest;
import com.taskflow.api.exception.NotFoundException;
import com.taskflow.api.repository.ProjectRepository;
import com.taskflow.api.repository.TaskRepository;
import com.taskflow.api.security.CurrentUser;

@Service
public class TaskService {

    private final ProjectRepository projects;
    private final TaskRepository tasks;
    private final CurrentUser currentUser;

    public TaskService(ProjectRepository projects, TaskRepository tasks, CurrentUser currentUser) {
        this.projects = projects;
        this.tasks = tasks;
        this.currentUser = currentUser;
    }
    public TaskResponse create(long projectId, CreateTaskRequest req) {
        String email = currentUser.email();
    
        Project project = projects.findByIdAndOwnerEmail(projectId, email)
                .orElseThrow(() -> new NotFoundException("Project not found"));
    
        Instant now = Instant.now();
        Task t = new Task(project, req.title(), TaskStatus.OPEN, req.dueDate(), now, now);
        return toResponse(tasks.save(t));
    }
    
    public Page<TaskResponse> list(long projectId, TaskStatus status, LocalDate dueBefore, Pageable pageable) {
        String email = currentUser.email();

        if (!projects.existsByIdAndOwnerEmail(projectId, email)) {
            throw new NotFoundException("Project not found");
        }
        if (status != null && dueBefore != null) {
            return tasks.findByProjectIdAndProjectOwnerEmailAndStatusAndDueDateBefore(projectId, email, status, dueBefore, pageable)
                    .map(this::toResponse);
        }
        if (status != null) {
            return tasks.findByProjectIdAndProjectOwnerEmailAndStatus(projectId, email, status, pageable)
                    .map(this::toResponse);
        }
        if (dueBefore != null) {
            return tasks.findByProjectIdAndProjectOwnerEmailAndDueDateBefore(projectId, email, dueBefore, pageable)
                    .map(this::toResponse);
        }
        return tasks.findByProjectIdAndProjectOwnerEmail(projectId, email, pageable)
                .map(this::toResponse);
    }
    
    public TaskResponse update(long taskId, UpdateTaskRequest req) {
        String email = currentUser.email();
    
        Task t = tasks.findByIdAndProjectOwnerEmail(taskId, email)
                .orElseThrow(() -> new NotFoundException("Task not found"));
    
        if (req.title() != null) t.setTitle(req.title());
        if (req.status() != null) t.setStatus(req.status());
        if (req.dueDate() != null) t.setDueDate(req.dueDate());
        t.setUpdatedAt(Instant.now());
    
        return toResponse(tasks.save(t));
    }
    
    public void delete(long taskId) {
        String email = currentUser.email();
    
        if (!tasks.existsByIdAndProjectOwnerEmail(taskId, email)) {
            throw new NotFoundException("Task not found");
        }
        tasks.deleteById(taskId);
    }

    private TaskResponse toResponse(Task t) {
        return new TaskResponse(
                t.getId(),
                t.getProject().getId(),
                t.getTitle(),
                t.getStatus(),
                t.getDueDate(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
