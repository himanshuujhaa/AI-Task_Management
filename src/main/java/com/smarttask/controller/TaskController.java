package com.smarttask.controller;
import com.smarttask.dto.DashboardResponse;
import com.smarttask.dto.TaskRequest;
import com.smarttask.dto.TaskResponse;
import com.smarttask.model.entity.Task;
import com.smarttask.model.entity.User;
import com.smarttask.model.enums.TaskPriority;
import com.smarttask.model.enums.TaskStatus;
import com.smarttask.repository.TaskRepository;
import com.smarttask.repository.UserRepository;
import com.smarttask.service.TaskAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.smarttask.constants.Constants.TASK_DELETED_SUCCESSFULLY;
import static com.smarttask.constants.Constants.USER_NOT_FOUND;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepository;

    private final TaskAiService taskAiService;
    private final UserRepository userRepository;

    @PostMapping
    public Task createTask(@RequestBody TaskRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));

        TaskPriority priority = request.getTaskPriority();

        if(priority == null) {
            priority = taskAiService.predictPriority(request.getDueDate(), request.getTitle(), request.getDescription());
        }

        TaskStatus status = request.getStatus();

        if(status == null) {
            status = TaskStatus.PENDING;
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .completed(false)
                .priority(priority)
                .status(status)
                .user(user)
                .build();

        return taskRepository.save(task);
    }

    private TaskResponse mapToResponse(Task task) {
        String userName = "name not provided";

        if(task.getUser() != null) {
            userName = task.getUser().getName();
        }

        return TaskResponse.builder()
                .id(task.getId()).title(task.getTitle()).description(task.getDescription())
                .completed(task.isCompleted()).dueDate(task.getDueDate()).createdAt(task.getCreatedAt())
                .status(task.getStatus()).priority(task.getPriority()).userName(userName)
                .build();

    }

    @GetMapping
    public List<TaskResponse> getAllTask() {
        return taskRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @GetMapping("/{id}")
//    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
//        return taskRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        return taskRepository.findById(id).map(task -> ResponseEntity.ok(mapToResponse(task)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody TaskRequest request) {
        return taskRepository.findById(id).map(task -> {
            task.setTitle(request.getTitle());
            task.setDescription(request.getDescription());
            task.setDueDate(request.getDueDate());
//            task.setPriority(request.getTaskPriority());
//            task.setStatus(request.getStatus());
            if(request.getStatus() != null) {
                task.setStatus(request.getStatus());
            }

            if(request.getTaskPriority() != null) {
                task.setPriority(request.getTaskPriority());
            }
            else {
                TaskPriority aiPriority = taskAiService.predictPriority(request.getDueDate(), request.getTitle(), request.getDescription());
                task.setPriority(aiPriority);
            }

            Task updated = taskRepository.save(task);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id){
        if(!taskRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        taskRepository.deleteById(id);

        return ResponseEntity.ok(TASK_DELETED_SUCCESSFULLY);
    }

    @GetMapping("/status/{status}")
    public List<Task> getByStatus(@PathVariable TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    @GetMapping("/priority/{priority}")
    public List<Task> getByPriority(@PathVariable TaskPriority priority) {
        return taskRepository.findByPriority(priority);
    }

    @GetMapping("/page")
    public Page<Task> getPaginatedTask(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    )
    {
        return taskRepository.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/search")
    public List<Task> searchTask(@RequestParam String keyword) { // used for /search?name=keyword
        return taskRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
    }

    @GetMapping("/taskstatus/overdue")
    public List<Task> getOverdueTask() {
        return taskRepository.findByDueDateBeforeAndStatusNot(LocalDateTime.now(), TaskStatus.COMPLETED);
    }

    @GetMapping("/dashboard")
    public DashboardResponse getDashboardResponse() {
        long total = taskRepository.count();

        long completed = taskRepository.countByStatus(TaskStatus.COMPLETED);
        long pending = taskRepository.countByStatus(TaskStatus.PENDING);

        long overdue = taskRepository.countByDueDateBeforeAndStatusNot(LocalDateTime.now(), TaskStatus.COMPLETED);
        long highPriority = taskRepository.countByPriority(TaskPriority.HIGH);

        return new DashboardResponse(total, completed, pending, overdue, highPriority);
    }

    @GetMapping("/user/{userId}")
    public List<Task> getTaskByUser(@PathVariable Long userId) {
        return taskRepository.findByUserId(userId);
    }
}
