package com.workoutapp.controller;

import com.workoutapp.dto.RegisterUserRequest;
import com.workoutapp.model.User;
import com.workoutapp.service.IUserService;
import com.workoutapp.util.JsonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @PostMapping
    public ResponseEntity<JsonResponse> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        User user = userService.createUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getBio()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(JsonResponse.of("user", user));
    }
}
