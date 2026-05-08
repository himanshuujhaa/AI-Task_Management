package com.smarttask.service;

import com.smarttask.dto.DashboardResponse;
import com.smarttask.dto.TaskRequest;
import com.smarttask.dto.TaskResponse;
import com.smarttask.model.entity.User;
import com.smarttask.model.enums.TaskPriority;
import com.smarttask.model.enums.TaskStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TaskService {

    TaskResponse createTask(TaskRequest request);

    List<TaskResponse> getAllTasks();

    TaskResponse getTaskById(Long id);
    TaskResponse updateTask(Long id, TaskRequest request);

    void deleteTask(Long id);

    List<TaskResponse> getTasksByStatus(TaskStatus status);

    List<TaskResponse> getTasksByPriority(TaskPriority priority);

    Page<TaskResponse> getPaginatedTasks(int page, int size);

    List<TaskResponse> searchTasks(String keyword);

    List<TaskResponse> getOverdueTasks();

    DashboardResponse getDashboard();

    List<TaskResponse> getTasksByUser(Long userId);


}
