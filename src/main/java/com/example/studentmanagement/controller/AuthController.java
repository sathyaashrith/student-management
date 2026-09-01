package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.ApiResponse;
import com.example.studentmanagement.dto.LoginRequest;
import com.example.studentmanagement.dto.LoginResponse;
import com.example.studentmanagement.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest request) {

        AuthService.LoginResult result =
                authService.login(
                        request.getUsername(),
                        request.getPassword()
                );

        LoginResponse loginResponse =
                new LoginResponse(
                        result.getAccessToken(),
                        result.getRefreshToken()
                );

        ApiResponse<LoginResponse> response =
                new ApiResponse<>(
                        true,
                        "Login successful",
                        loginResponse
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @RequestBody RefreshTokenRequest request) {

        String newAccessToken =
                authService.refreshAccessToken(
                        request.getRefreshToken()
                );

        LoginResponse response =
                new LoginResponse(
                        newAccessToken,
                        request.getRefreshToken()
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Access token refreshed successfully",
                        response
                )
        );
    }

    public static class RefreshTokenRequest {

        private String refreshToken;

        public RefreshTokenRequest() {
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}