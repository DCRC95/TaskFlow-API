package com.taskflow.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.taskflow.api.domain.Task;
import com.taskflow.api.domain.TaskStatus;
import com.taskflow.api.dto.AuthResponse;
import com.taskflow.api.dto.RegisterRequest;
import com.taskflow.api.repository.TaskRepository;

class AnalyticsIT extends IntegrationTestBase {

    @Autowired
    TestRestTemplate http;

    @Autowired
    TaskRepository tasks;

    @Test
    void productivitySummary_returnsAggregatedMetricsForCurrentUser() {
        ResponseEntity<AuthResponse> reg = http.postForEntity(
                "/api/v1/auth/register",
                new RegisterRequest("analytics@example.com", "Password123!"),
                AuthResponse.class
        );
        assertThat(reg.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reg.getBody()).isNotNull();

        String token = reg.getBody().token();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> projectReq = new HttpEntity<>(
                "{\"name\":\"Analytics Project\",\"description\":\"With tasks\"}",
                headers
        );

        ResponseEntity<Map> createdProject = http.postForEntity(
                "/api/v1/projects",
                projectReq,
                Map.class
        );
        assertThat(createdProject.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        @SuppressWarnings("unchecked")
        Map<String, Object> createdProjectBody = createdProject.getBody();
        Long projectId = ((Number) createdProjectBody.get("id")).longValue();

        for (int i = 0; i < 5; i++) {
            HttpEntity<String> taskReq = new HttpEntity<>(
                    "{\"title\":\"Task " + i + "\",\"dueDate\":null}",
                    headers
            );
            ResponseEntity<Map> createdTask = http.postForEntity(
                    "/api/v1/projects/" + projectId + "/tasks",
                    taskReq,
                    Map.class
            );
            assertThat(createdTask.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }

        List<Task> allTasks = tasks.findAll();
        assertThat(allTasks).hasSize(5);

        Task done = allTasks.get(0);
        done.setStatus(TaskStatus.DONE);
        Task overdue = allTasks.get(1);
        overdue.setStatus(TaskStatus.OVERDUE);
        tasks.saveAll(List.of(done, overdue));

        LocalDate from = LocalDate.now().minusDays(1);
        LocalDate to = LocalDate.now().plusDays(1);

        String url = String.format("/api/v1/analytics/summary?from=%s&to=%s", from, to);

        HttpEntity<Void> analyticsReq = new HttpEntity<>(headers);
        ResponseEntity<Map> summaryRes = http.exchange(
                url,
                HttpMethod.GET,
                analyticsReq,
                Map.class
        );

        assertThat(summaryRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = summaryRes.getBody();
        assertThat(body).isNotNull();

        Number tasksCreated = (Number) body.get("tasksCreated");
        Number tasksCompleted = (Number) body.get("tasksCompleted");
        Number tasksOverdue = (Number) body.get("tasksOverdue");

        assertThat(tasksCreated.longValue()).isGreaterThanOrEqualTo(5);
        assertThat(tasksCompleted.longValue()).isGreaterThanOrEqualTo(1);
        assertThat(tasksOverdue.longValue()).isGreaterThanOrEqualTo(1);
    }
}


