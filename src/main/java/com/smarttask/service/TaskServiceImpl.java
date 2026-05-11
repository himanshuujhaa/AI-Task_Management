package com.smarttask.service;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.smarttask.dto.DashboardResponse;
import com.smarttask.dto.TaskRequest;
import com.smarttask.dto.TaskResponse;
import com.smarttask.exception.ResourceNotFoundException;
import com.smarttask.model.entity.Task;
import com.smarttask.model.entity.User;
import com.smarttask.model.enums.TaskPriority;
import com.smarttask.model.enums.TaskStatus;
import com.smarttask.repository.TaskRepository;
import com.smarttask.repository.UserRepository;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.smarttask.constants.Constants.*;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskAiService taskAiService;

    private TaskResponse mapToResponse(Task task) {
        String userName = NAME_NOT_FOUND;

        if(task.getUser() != null && task.getUser().getName() != null) {
            userName = task.getUser().getName();
        }

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.isCompleted())
                .status(task.getStatus())
                .dueDate(task.getDueDate())
                .priority(task.getPriority())
                .createdAt(task.getCreatedAt())
                .userName(userName)
                .build();

    }
    @Override
    public TaskResponse createTask(TaskRequest request, Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        TaskPriority priority = request.getTaskPriority();

        if(priority == null) {
            priority = taskAiService.predictPriority(
                    request.getDueDate(), request.getTitle(), request.getDescription()
            );
        }

        TaskStatus status = request.getStatus();

        if(status == null) {
            status = TaskStatus.PENDING;
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .completed(false)
                .dueDate(request.getDueDate())
                .status(status)
                .priority(priority)
                .user(user)
                .build();

        Task savedTask = taskRepository.save(task);

        return mapToResponse(savedTask);
    }

    @Override
    public List<TaskResponse> getAllTasks(Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
        return taskRepository.findByUser(user).stream().map(this::mapToResponse).toList();
//        return taskRepository.findAll()
//                .stream()
//                .map(this::mapToResponse)
//                .toList();
    }

    @Override
    public TaskResponse getTaskById(Long id, Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND));

        if(!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(ACCESS_DENIED);
        }

        return mapToResponse(task);
    }

    @Override
    public TaskResponse updateTask(Long id, TaskRequest request, Authentication authentication) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());

        if(request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        else {
            task.setStatus(TaskStatus.PENDING);
        }

//        if(task.getPriority() != null) {
//            task.setPriority(task.getPriority());
//        }

        if(request.getTaskPriority() != null) {
            task.setPriority(request.getTaskPriority());
        }
        else {
            TaskPriority aiPriority = taskAiService.predictPriority(request.getDueDate(), request.getTitle(), request.getDescription());
            task.setPriority(aiPriority);
        }

        Task updatedTask = taskRepository.save(task);

        return mapToResponse(updatedTask);
    }

    @Override
    public void deleteTask(Long id, Authentication authentication) {
        if(!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException(TASK_NOT_FOUND);
        }
        taskRepository.deleteById(id);
    }

    @Override
    public List<TaskResponse> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status).stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<TaskResponse> getTasksByPriority(TaskPriority priority) {
        return taskRepository.findByPriority(priority).stream().map(this::mapToResponse).toList();
    }

    @Override
    public Page<TaskResponse> getPaginatedTasks(int page, int sz) {

        Page<Task> taskPage = taskRepository.findAll(PageRequest.of(page, sz));

        List<TaskResponse> responses = taskPage.getContent().stream().map(this::mapToResponse).toList();

        return new PageImpl<>(responses, taskPage.getPageable(), taskPage.getTotalElements());
    }

    @Override
    public List<TaskResponse> searchTasks(String keyword) {
        return taskRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<TaskResponse> getOverdueTasks() {
        return taskRepository.findByDueDateBeforeAndStatusNot(LocalDateTime.now(), TaskStatus.COMPLETED)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public DashboardResponse getDashboard() {
        long totalTask = taskRepository.count();
        long completedTask = taskRepository.countByStatus(TaskStatus.COMPLETED);
        long pendingTask = taskRepository.countByStatus(TaskStatus.PENDING);
        long inProgressTask = taskRepository.countByStatus(TaskStatus.IN_PROGRESS);
        long highPriorityTask = taskRepository.countByPriority(TaskPriority.HIGH);
        long lowPriorityTask = taskRepository.countByPriority(TaskPriority.LOW);
        long overDueTask = taskRepository.countByDueDateBeforeAndStatusNot(LocalDateTime.now(), TaskStatus.COMPLETED);

        return new DashboardResponse(totalTask, completedTask, pendingTask, inProgressTask, overDueTask, highPriorityTask, lowPriorityTask);
    }

    @Override
    public List<TaskResponse> getTasksByUser(Long userId) {

        if(!userRepository.existsById(userId)) {
            System.out.println(USER_NOT_FOUND);
            throw new ResourceNotFoundException(USER_NOT_FOUND);
        }

        return taskRepository.findByUserId(userId).stream().map(this::mapToResponse).toList();
    }

}
