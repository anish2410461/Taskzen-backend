package org.example.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsResponse {

    private long totalTasks;

    private long completedTasks;

    private long pendingTasks;

    private double completionRate;

    private long overdueTasks;

    private long upcomingTasks;
}
