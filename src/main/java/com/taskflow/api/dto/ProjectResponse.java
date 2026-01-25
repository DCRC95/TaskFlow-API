package com.taskflow.api.dto;

import java.time.Instant;

public record ProjectResponse(
        long id,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt
) {}
