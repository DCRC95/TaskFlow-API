package com.taskflow.api.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
        @NotBlank @Size(min = 2, max = 200) String title,
        LocalDate dueDate
) {}
