package com.taskflow.api.dto;

import jakarta.validation.constraints.Size;

public record UpdateProjectRequest(
        @Size(min = 2, max = 80) String name,
        @Size(max = 500) String description
) {}
