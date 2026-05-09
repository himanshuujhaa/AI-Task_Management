package com.smarttask.controller;

import com.smarttask.dto.UserRequest;
import com.smarttask.dto.UserResponse;
import com.smarttask.exception.ResourceNotFoundException;
import com.smarttask.model.entity.Task;
import com.smarttask.model.entity.User;
import com.smarttask.repository.TaskRepository;
import com.smarttask.repository.UserRepository;
import com.smarttask.service.UserService;
import com.smarttask.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.List;

import static com.smarttask.constants.Constants.USER_DELETED_SUCCESSFULLY;
import static com.smarttask.constants.Constants.USER_NOT_FOUND;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

//    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    private final UserService userService;


//    @PostMapping
//    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
//        return ResponseEntity.ok(userService.createUser(user));
//    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping()
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        if(!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(USER_NOT_FOUND);
        }
        userService.deleteUser(userId);

        return ResponseEntity.ok(USER_DELETED_SUCCESSFULLY);
    }
}
