package com.workoutapp.controller;

import com.workoutapp.dto.AuthTokenResponse;
import com.workoutapp.dto.CreateTokenRequest;
import com.workoutapp.model.User;
import com.workoutapp.service.ITokenService;
import com.workoutapp.service.IUserService;
import com.workoutapp.util.JsonResponse;
import com.workoutapp.util.PasswordUtil;
import com.workoutapp.util.TokenUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tokens/authentication")
@RequiredArgsConstructor
public class TokenController {
    private final ITokenService tokenService;
    private final IUserService userService;
    private final PasswordUtil passwordUtil;

    @PostMapping
    public ResponseEntity<JsonResponse> createToken(@Valid @RequestBody CreateTokenRequest request) {
        User user = userService.getUserByUsername(request.getUsername())
                .orElse(null);

        if (user == null || !passwordUtil.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(JsonResponse.of("error", "invalid credentials"));
        }

        TokenUtil.TokenData tokenData = tokenService.createNewToken(user.getId(), 24);
        AuthTokenResponse response = new AuthTokenResponse(
                tokenData.getPlaintext(),
                tokenData.getExpiry()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(JsonResponse.of("auth_token", response));
    }
}
