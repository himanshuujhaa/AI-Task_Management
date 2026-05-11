package com.smarttask.dto;

import com.smarttask.model.enums.TaskPriority;
import com.smarttask.model.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.smarttask.constants.Constants.*;

@Getter
@Setter
public class TaskRequest {

    @NotBlank(message = TITLE_REQUIRED)
    private String title;

    private String description;

    @NotNull(message = DUEDATE_REQUIRED)
    private LocalDateTime dueDate;

    private TaskStatus status;
    private TaskPriority taskPriority;

//    @NotBlank(message = USERID_REQUIRED)
//    private Long userId;

}
