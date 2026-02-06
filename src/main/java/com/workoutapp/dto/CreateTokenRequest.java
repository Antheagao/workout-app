package com.workoutapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTokenRequest {
    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "password is required")
    private String password;
}
