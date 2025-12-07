package com.alertix.security;

import com.alertix.config.AlertixProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT Token Provider
 *
 * Responsible for:
 * - Generating JWT access and refresh tokens
 * - Validating tokens
 * - Extracting user information from tokens
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final AlertixProperties alertixProperties;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        String secret = alertixProperties.getJwt().getSecret();
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate access token
     */
    public String generateAccessToken(Authentication authentication) {
        String username = authentication.getName();
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + alertixProperties.getJwt().getAccessTokenExpiration());

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .claim("type", "access")
                .issuer(alertixProperties.getJwt().getIssuer())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Generate refresh token
     */
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + alertixProperties.getJwt().getRefreshTokenExpiration());

        return Jwts.builder()
                .subject(username)
                .claim("type", "refresh")
                .issuer(alertixProperties.getJwt().getIssuer())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Get username from JWT token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Get roles from token
     */
    public String getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("roles", String.class);
    }
}
