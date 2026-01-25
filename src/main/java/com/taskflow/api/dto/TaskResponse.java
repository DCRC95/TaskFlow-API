package com.taskflow.api.dto;

import java.time.Instant;
import java.time.LocalDate;

import com.taskflow.api.domain.TaskStatus;

public record TaskResponse(
        long id,
        long projectId,
        String title,
        TaskStatus status,
        LocalDate dueDate,
        Instant createdAt,
        Instant updatedAt
) {}
