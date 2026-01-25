package com.taskflow.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/v1/health")
    public Message health() {
        return new Message("TaskFlow API is running");
    }

    public record Message(String message) {}
}
