package com.smarttask.dto;

import com.smarttask.model.enums.TaskPriority;
import com.smarttask.model.enums.TaskStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskRequest {

    private String title;
    private String description;
    private LocalDateTime dueDate;
    private TaskStatus status;
    private TaskPriority taskPriority;

    private Long userId;

}
