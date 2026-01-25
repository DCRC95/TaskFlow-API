package com.taskflow.api.dto;

import java.time.LocalDate;

import com.taskflow.api.domain.TaskStatus;

import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(
        @Size(min = 2, max = 200) String title,
        TaskStatus status,
        LocalDate dueDate
) {}
