package com.taskflow.api.dto;

import java.util.List;

public record AnalyticsSummaryResponse(
        long tasksCreated,
        long tasksCompleted,
        long tasksOverdue,
        double completionRate,
        List<TopProjectOpenTasksItem> topProjectsByOpenTasks
) {

    public record TopProjectOpenTasksItem(
            long projectId,
            String projectName,
            long openTasks
    ) {}
}


