package com.workoutapp.service;

import com.workoutapp.util.TokenUtil;

import java.util.Optional;

public interface ITokenService {
    TokenUtil.TokenData createNewToken(Long userId, long ttlHours);
    Optional<Long> getUserIdByToken(String plaintextToken, String scope);
    void deleteAllTokensForUser(Long userId, String scope);
}
