package com.smarttask.dto;

import com.smarttask.model.enums.TaskPriority;
import com.smarttask.model.enums.TaskStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private TaskStatus status;
    private TaskPriority priority;
    private String userName;
}
