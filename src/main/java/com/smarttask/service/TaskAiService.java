package com.smarttask.service;

import com.smarttask.model.enums.TaskPriority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

//import static java.lang.StringUTF16.toLowerCase;
import java.util.*;

import static com.smarttask.constants.Constants.*;

@Service
public class TaskAiService {
    public TaskPriority predictPriority(LocalDateTime dueDate, String title, String description) {
        if(dueDate == null) {
            return TaskPriority.LOW;
        }

        long hoursLeft = Duration.between(LocalDateTime.now(), dueDate).toHours();

        if(hoursLeft <= 24) {
            return TaskPriority.HIGH;
        }

        if(hoursLeft <= 72) {
            return  TaskPriority.MEDIUM;
        }

        String text = ((title == null ? "" : title) + " " + (description == null ? "" : description)).toLowerCase();

        if(text.contains(URGENT) || text.contains(IMPORTANT) || text.contains(ASAP)) {
            return TaskPriority.HIGH;
        }
        return TaskPriority.LOW;
    }
}
