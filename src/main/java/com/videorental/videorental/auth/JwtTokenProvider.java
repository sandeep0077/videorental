package com.videorental.videorental.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generate JWT Token with custom claims.
     *
     * @param email User's email (subject)
     * @return A signed JWT token.
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)                                      // Set email as subject
                .issuedAt(new Date(System.currentTimeMillis()))                                // Token issued time
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Expiration time
                .signWith(secretKey, Jwts.SIG.HS256)          // Sign with the secure key
                .compact();
    }

    // Extract email from token
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Validate JWT token
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }
}