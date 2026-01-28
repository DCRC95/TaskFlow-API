package com.taskflow.api.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.taskflow.api.domain.TaskStatus;
import com.taskflow.api.dto.AnalyticsSummaryResponse;
import com.taskflow.api.dto.AnalyticsSummaryResponse.TopProjectOpenTasksItem;
import com.taskflow.api.exception.BadRequestException;
import com.taskflow.api.repository.TaskRepository;
import com.taskflow.api.repository.TopProjectOpenTasksView;
import com.taskflow.api.security.CurrentUser;

@Service
public class AnalyticsService {

    private final TaskRepository tasks;
    private final CurrentUser currentUser;

    public AnalyticsService(TaskRepository tasks, CurrentUser currentUser) {
        this.tasks = tasks;
        this.currentUser = currentUser;
    }

    public AnalyticsSummaryResponse getSummary(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new BadRequestException("Both 'from' and 'to' query parameters are required");
        }
        if (from.isAfter(to)) {
            throw new BadRequestException("'from' date must be on or before 'to' date");
        }

        String email = currentUser.email();

        ZoneId zone = ZoneId.systemDefault();
        Instant fromInstant = from.atStartOfDay(zone).toInstant();
        Instant toInstant = to.plusDays(1).atStartOfDay(zone).toInstant();

        long created = tasks.countCreatedByOwnerAndCreatedAtBetween(email, fromInstant, toInstant);
        long completed = tasks.countCompletedByOwnerAndUpdatedAtBetween(email, fromInstant, toInstant);
        long overdue = tasks.countOverdueByOwnerAndUpdatedAtBetween(email, fromInstant, toInstant);

        double completionRate = created == 0 ? 0.0 : (double) completed / (double) created;

        List<TaskStatus> openStatuses = List.of(TaskStatus.OPEN, TaskStatus.IN_PROGRESS);
        List<TopProjectOpenTasksView> topProjects = tasks.findTopProjectsByOwnerAndOpenStatuses(
                email,
                openStatuses,
                PageRequest.of(0, 3)
        );

        List<TopProjectOpenTasksItem> topProjectDtos = topProjects.stream()
                .map(view -> new TopProjectOpenTasksItem(
                        view.getProjectId(),
                        view.getProjectName(),
                        view.getOpenTasks()
                ))
                .toList();

        return new AnalyticsSummaryResponse(
                created,
                completed,
                overdue,
                completionRate,
                topProjectDtos
        );
    }
}


