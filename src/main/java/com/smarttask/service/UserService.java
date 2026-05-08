package com.smarttask.service;

import com.smarttask.model.entity.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    List<User> getAllUsers();

    void deleteUser(Long id);

    User getUserById(Long id);
}
