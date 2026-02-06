package com.workoutapp.util;

import org.springframework.stereotype.Component;

import org.apache.commons.codec.binary.Base32;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Component
public class TokenUtil {
    public static final String SCOPE_AUTH = "authentication";

    public TokenData generateToken(Long userId, long ttlHours) {
        try {
            // Generate random bytes
            byte[] randomBytes = new byte[32];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(randomBytes);

            // Encode to base32
            Base32 base32 = new Base32();
            String plaintext = base32.encodeAsString(randomBytes).replace("=", "");

            // Hash the token
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plaintext.getBytes(StandardCharsets.UTF_8));

            LocalDateTime expiry = LocalDateTime.now().plusHours(ttlHours);

            return new TokenData(plaintext, hash, userId, expiry);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate token", e);
        }
    }

    public byte[] hashToken(String plaintextToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(plaintextToken.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }

    public static class TokenData {
        private final String plaintext;
        private final byte[] hash;
        private final Long userId;
        private final LocalDateTime expiry;

        public TokenData(String plaintext, byte[] hash, Long userId, LocalDateTime expiry) {
            this.plaintext = plaintext;
            this.hash = hash;
            this.userId = userId;
            this.expiry = expiry;
        }

        public String getPlaintext() {
            return plaintext;
        }

        public byte[] getHash() {
            return hash;
        }

        public Long getUserId() {
            return userId;
        }

        public LocalDateTime getExpiry() {
            return expiry;
        }
    }
}
