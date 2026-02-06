package com.workoutapp.service;

import com.workoutapp.model.Token;
import com.workoutapp.repository.TokenRepository;
import com.workoutapp.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService {
    private final TokenRepository tokenRepository;
    private final TokenUtil tokenUtil;

    @Transactional
    public TokenUtil.TokenData createNewToken(Long userId, long ttlHours) {
        TokenUtil.TokenData tokenData = tokenUtil.generateToken(userId, ttlHours);

        Token token = new Token();
        token.setHash(tokenData.getHash());
        token.setUserId(userId);
        token.setExpiry(tokenData.getExpiry());
        token.setScope(TokenUtil.SCOPE_AUTH);

        tokenRepository.save(token);

        return tokenData;
    }

    public Optional<Long> getUserIdByToken(String plaintextToken, String scope) {
        byte[] hash = tokenUtil.hashToken(plaintextToken);
        return tokenRepository.findUserIdByTokenHash(hash, scope, LocalDateTime.now());
    }

    @Transactional
    public void deleteAllTokensForUser(Long userId, String scope) {
        tokenRepository.deleteByUserIdAndScope(userId, scope);
    }
}
