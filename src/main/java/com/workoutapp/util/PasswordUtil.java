package com.workoutapp.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public String hashPassword(String plaintextPassword) {
        return encoder.encode(plaintextPassword);
    }

    public boolean matches(String plaintextPassword, String hashedPassword) {
        return encoder.matches(plaintextPassword, hashedPassword);
    }
}
