package com.smarttask.service;

import com.smarttask.dto.UserRequest;
import com.smarttask.dto.UserResponse;
import com.smarttask.model.entity.User;

import java.util.List;

public interface UserService {

//    User createUser(User user);

    UserResponse createUser(UserRequest request);

    List<UserResponse> getAllUsers();

    void deleteUser(Long id);

    UserResponse getUserById(Long id);
}
