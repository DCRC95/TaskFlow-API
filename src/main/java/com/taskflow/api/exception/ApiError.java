package com.taskflow.api.exception;

import java.time.Instant;
import java.util.Map;

/**
 * Standard API error response envelope used by global exception handling.
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> validationErrors
) {
}


