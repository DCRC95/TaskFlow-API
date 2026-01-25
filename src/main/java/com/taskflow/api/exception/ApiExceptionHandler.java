package com.taskflow.api.exception;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    record FieldError(String field, String message) {}
    record ErrorResponse(Instant timestamp, int status, String error, String path, String message, List<FieldError> details) {}

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, jakarta.servlet.http.HttpServletRequest req) {
        var body = new ErrorResponse(
                Instant.now(),
                404,
                "Not Found",
                req.getRequestURI(),
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, jakarta.servlet.http.HttpServletRequest req) {
        var details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        var body = new ErrorResponse(
                Instant.now(),
                400,
                "Validation Failed",
                req.getRequestURI(),
                "Request validation failed",
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
public org.springframework.http.ResponseEntity<?> handleBadRequest(IllegalArgumentException ex,
        jakarta.servlet.http.HttpServletRequest req) {

    record Error(java.time.Instant timestamp, int status, String error, String path, String message) {}
    var body = new Error(java.time.Instant.now(), 400, "Bad Request", req.getRequestURI(), ex.getMessage());
    return org.springframework.http.ResponseEntity.badRequest().body(body);
}

}
