package com.example.studentmanagement.service;

import com.example.studentmanagement.entity.RefreshToken;
import com.example.studentmanagement.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final Logger log =
            LoggerFactory.getLogger(RefreshTokenService.class);

    @Value("${jwt.refresh-token-expiration-days}")
    private long refreshTokenDays;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository) {

        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(String username) {

        log.info("Creating refresh token for user: {}", username);

        // Remove previous refresh token for this user
        refreshTokenRepository.deleteByUsername(username);

        String token = UUID.randomUUID().toString();

        LocalDateTime expiryDate =
                LocalDateTime.now()
                        .plusDays(refreshTokenDays);

        RefreshToken refreshToken =
                new RefreshToken(
                        token,
                        username,
                        expiryDate
                );

        RefreshToken savedToken =
                refreshTokenRepository.save(refreshToken);

        log.info(
                "Refresh token created successfully for user: {}",
                username
        );

        return savedToken;
    }

    public RefreshToken findByToken(String token) {

        return refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() -> {

                    log.warn("Invalid refresh token received");

                    return new RuntimeException(
                            "Invalid refresh token"
                    );
                });
    }

    public boolean isExpired(RefreshToken refreshToken) {

        boolean expired =
                refreshToken.getExpiryDate()
                        .isBefore(LocalDateTime.now());

        if (expired) {
            log.warn(
                    "Refresh token expired for user: {}",
                    refreshToken.getUsername()
            );
        }

        return expired;
    }

    @Transactional
    public void deleteToken(RefreshToken refreshToken) {

        log.info(
                "Deleting refresh token for user: {}",
                refreshToken.getUsername()
        );

        refreshTokenRepository.delete(refreshToken);
    }
}