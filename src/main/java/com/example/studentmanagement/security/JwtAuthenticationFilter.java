package com.example.studentmanagement.security;

import com.example.studentmanagement.service.JwtService;
import com.example.studentmanagement.service.CustomUserDetailsService;

import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(
                    JwtAuthenticationFilter.class
            );

    private final JwtService jwtService;

    private final CustomUserDetailsService
            customUserDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService customUserDetailsService) {

        this.jwtService = jwtService;
        this.customUserDetailsService =
                customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        log.debug(
                "JWT filter processing request: {} {}",
                request.getMethod(),
                request.getRequestURI()
        );

        String authHeader =
                request.getHeader("Authorization");

        // No Authorization header
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            log.debug("No Bearer token found");

            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken =
                authHeader.substring(7);

        try {

            String username =
                    jwtService.extractUsername(jwtToken);

            if (username != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {

                UserDetails userDetails =
                        customUserDetailsService
                                .loadUserByUsername(username);

                if (jwtService.isTokenValid(
                        jwtToken,
                        userDetails)) {

                    UsernamePasswordAuthenticationToken
                            authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(
                                    authentication
                            );

                    log.debug(
                            "JWT authentication successful for user: {}",
                            username
                    );
                }
            }

            filterChain.doFilter(request, response);

        } catch (JwtException | IllegalArgumentException exception) {

            log.warn(
                    "Invalid or expired JWT received"
            );

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(
                    """
                    {
                        "success": false,
                        "message": "Invalid or expired token",
                        "data": null
                    }
                    """
            );
        }
    }
}