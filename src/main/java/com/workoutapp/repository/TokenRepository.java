package com.workoutapp.repository;

import com.workoutapp.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, byte[]> {
    @Query("SELECT t.userId FROM Token t WHERE t.hash = :hash AND t.scope = :scope AND t.expiry > :now")
    Optional<Long> findUserIdByTokenHash(@Param("hash") byte[] hash, @Param("scope") String scope, @Param("now") LocalDateTime now);

    void deleteByUserIdAndScope(Long userId, String scope);
}
