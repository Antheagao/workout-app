package com.workoutapp.service;

import com.workoutapp.exception.ConflictException;
import com.workoutapp.model.User;
import com.workoutapp.repository.UserRepository;
import com.workoutapp.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;

    @Transactional
    public User createUser(String username, String email, String password, String bio) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordUtil.hashPassword(password));
        if (bio != null && !bio.isEmpty()) {
            user.setBio(bio);
        }

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            String errorMessage = e.getMessage().toLowerCase();
            if (errorMessage.contains("username") && errorMessage.contains("unique")) {
                throw new ConflictException("username already exists");
            }
            if (errorMessage.contains("email") && errorMessage.contains("unique")) {
                throw new ConflictException("email already exists");
            }
            throw new RuntimeException("Failed to create user", e);
        }
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}
