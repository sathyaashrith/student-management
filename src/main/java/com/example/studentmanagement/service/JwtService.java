package com.example.studentmanagement.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(String username) {

        Date now = new Date();

        Date expiration =
                new Date(
                        now.getTime()
                                + accessTokenExpiration
                );

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }

    private <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        Claims claims =
                Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        String username =
                extractUsername(token);

        return username.equals(
                userDetails.getUsername()
        ) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {

        Date expiration =
                extractClaim(
                        token,
                        Claims::getExpiration
                );

        return expiration.before(new Date());
    }
}