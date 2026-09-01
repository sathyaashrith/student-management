package com.example.studentmanagement.config;

import com.example.studentmanagement.security.CustomAccessDeniedHandler;
import com.example.studentmanagement.security.CustomAuthenticationEntryPoint;
import com.example.studentmanagement.security.JwtAuthenticationFilter;
import com.example.studentmanagement.service.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomAuthenticationEntryPoint authenticationEntryPoint,
            CustomAccessDeniedHandler accessDeniedHandler) {

        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        customUserDetailsService
                );

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http

                // CORS
                .cors(cors -> {})

                // JWT based API - CSRF disabled
                .csrf(csrf -> csrf.disable())

                // Stateless authentication
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Authentication / Authorization errors
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                authenticationEntryPoint
                        )
                        .accessDeniedHandler(
                                accessDeniedHandler
                        )
                )

                // Security headers
                .headers(headers -> headers

                        .frameOptions(frame ->
                                frame.deny()
                        )

                        .contentTypeOptions(contentType ->
                        {
                        })

                        .httpStrictTransportSecurity(hsts ->
                                hsts
                                        .includeSubDomains(true)
                                        .preload(true)
                                        .maxAgeInSeconds(31536000)
                        )
                )

                // Endpoint authorization
                .authorizeHttpRequests(auth -> auth

                        // Authentication endpoints
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/refresh"
                        ).permitAll()

                        // ADMIN-only operations
                        .requestMatchers(
                                "/api/students/**"
                        ).hasAnyRole(
                                "USER",
                                "ADMIN"
                        )

                        // Everything else requires authentication
                        .anyRequest()
                        .authenticated()
                )

                // Authentication provider
                .authenticationProvider(
                        authenticationProvider()
                )

                // JWT filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}