package com.smarttask.controller;

import com.smarttask.model.entity.Task;
import com.smarttask.model.entity.User;
import com.smarttask.repository.TaskRepository;
import com.smarttask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

//    private final TaskRepository taskRepository;

    private final UserRepository userRepository;


    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/{userId}")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
