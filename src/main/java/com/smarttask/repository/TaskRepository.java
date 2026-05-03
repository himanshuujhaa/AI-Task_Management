package com.smarttask.repository;

import com.smarttask.model.entity.Task;
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

    List<Task> findByTitleOrDescription(String title, String desc);

    List<Task> findByDurDateBeforeIncompleteStatus(LocalDateTime now, TaskStatus status);
}
