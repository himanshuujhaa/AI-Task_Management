package com.smarttask.repository;

import com.smarttask.dto.DashboardResponse;
import com.smarttask.model.entity.Task;
import com.smarttask.model.entity.User;
import com.smarttask.model.enums.TaskPriority;
import com.smarttask.model.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(TaskStatus taskStatus);

    List<Task> findByPriority(TaskPriority taskPriority);

    Page<Task> findAll(Pageable pageable);

    List<Task> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String desc);

    List<Task> findByDueDateBeforeAndStatusNot(LocalDateTime now, TaskStatus status);

    long countByStatus(TaskStatus status);
    long countByPriority(TaskPriority priority);

    long countByDueDateBeforeAndStatusNot(LocalDateTime now, TaskStatus status);

    List<Task> findByUserId(Long UserId);

    List<Task> findByUser(User user);

}
