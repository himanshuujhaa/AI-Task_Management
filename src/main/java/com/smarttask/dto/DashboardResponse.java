package com.smarttask.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DashboardResponse {
    private long totalTask;
    private long completedTask;
    private long pendingTask;
    private long inProgressTask;
    private long overDueTask;
    private long highPriorityTask;
    private long lowPriorityTask;
}
