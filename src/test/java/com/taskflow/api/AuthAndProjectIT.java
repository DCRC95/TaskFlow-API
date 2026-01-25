package com.taskflow.api;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.taskflow.api.dto.AuthResponse;
import com.taskflow.api.dto.RegisterRequest;

public class AuthAndProjectIT extends IntegrationTestBase {

    @Autowired
    TestRestTemplate http;

    @Test
    void register_then_createProject_requiresToken_and_succeedsWithToken() {
        // Register
        ResponseEntity<AuthResponse> reg = http.postForEntity(
                "/api/v1/auth/register",
                new RegisterRequest("a@example.com", "Password123!"),
                AuthResponse.class
        );
        assertThat(reg.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reg.getBody()).isNotNull();

        String token = reg.getBody().token();

        // Create project with token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> req = new HttpEntity<>(
                "{\"name\":\"Project A\",\"description\":\"Owned by A\"}",
                headers
        );

        ResponseEntity<String> created = http.exchange(
                "/api/v1/projects",
                HttpMethod.POST,
                req,
                String.class
        );

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).contains("Project A");
    }

    @Test
    void createProject_withoutToken_isBlocked() {
        ResponseEntity<String> res = http.postForEntity(
                "/api/v1/projects",
                Map.of("name", "NoToken", "description", "Should fail"),
                String.class
        );
        // Depending on your config you may see 401 or 403
        assertThat(res.getStatusCode().value()).isIn(401, 403);
    }
}
