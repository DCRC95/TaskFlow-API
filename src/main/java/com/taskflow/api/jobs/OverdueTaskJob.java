package com.taskflow.api.jobs;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.taskflow.api.domain.Task;
import com.taskflow.api.domain.TaskStatus;
import com.taskflow.api.repository.TaskRepository;

@Component
public class OverdueTaskJob {

    private final TaskRepository tasks;

    public OverdueTaskJob(TaskRepository tasks) {
        this.tasks = tasks;
    }

    // Every 5 minutes (employer-friendly: not too spammy)
    @Scheduled(fixedDelayString = "PT5M")
    @Transactional
    public void markOverdue() {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());

        // Find tasks whose due date is before today and not DONE
        List<Task> candidates = tasks.findByDueDateBeforeAndStatusNot(today, TaskStatus.DONE);

        Instant now = Instant.now();
        for (Task t : candidates) {
            // Only mark non-done tasks as overdue
            if (t.getStatus() != TaskStatus.DONE && t.getStatus() != TaskStatus.OVERDUE) {
                t.setStatus(TaskStatus.OVERDUE);
                t.setUpdatedAt(now);
            }
        }
        // No explicit save needed: @Transactional + JPA dirty checking updates rows
    }
}
