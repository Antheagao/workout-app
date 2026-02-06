package com.workoutapp.service;

import com.workoutapp.model.User;

import java.util.Optional;

public interface IUserService {
    User createUser(String username, String email, String password, String bio);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserById(Long id);
}
