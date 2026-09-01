package com.example.studentmanagement.service;

import com.example.studentmanagement.entity.RefreshToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class AuthService {
    private static final Logger log =
            LoggerFactory.getLogger(AuthService.class);
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RefreshTokenService refreshTokenService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public LoginResult login(
            String username,
            String password) {
        log.info("Login attempt for username: {}", username);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );
        log.info(
                "Authentication successful for username: {}",
                username
        );
        String accessToken =
                jwtService.generateToken(username);
        log.info(
                "Access token generated for username: {}",
                username
        );
        RefreshToken refreshToken =
                refreshTokenService
                        .createRefreshToken(username);
        log.info(
                "Refresh token generated for username: {}",
                username
        );
        return new LoginResult(
                accessToken,
                refreshToken.getToken()
        );
    }

    public String refreshAccessToken(String refreshTokenValue) {
        log.info("Attempting to refresh access token");
        RefreshToken refreshToken =
                refreshTokenService
                        .findByToken(refreshTokenValue);
        log.info(
                "Refresh token found for user: {}",
                refreshToken.getUsername()
        );
        log.warn(
                "Refresh token expired for user: {}",
                refreshToken.getUsername()
        );
        if (refreshTokenService.isExpired(refreshToken)) {

            refreshTokenService.deleteToken(refreshToken);

            throw new RuntimeException(
                    "Refresh token has expired"
            );
        }
        log.info(
                "New access token generated for user: {}",
                refreshToken.getUsername()
        );
        return jwtService.generateToken(
                refreshToken.getUsername()

        );


    }

    public static class LoginResult {

        private final String accessToken;
        private final String refreshToken;

        public LoginResult(
                String accessToken,
                String refreshToken) {

            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }
    }
}