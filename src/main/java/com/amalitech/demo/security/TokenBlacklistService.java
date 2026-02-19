package com.amalitech.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Service to manage token blacklisting for logout functionality.
 * Uses Caffeine cache with TTL matching token expiration time.
 * When a user logs out, their token is added to the blacklist.
 * The JwtAuthenticationFilter checks this blacklist before validating tokens.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final JwtService jwtService;
    private final CacheManager cacheManager;

    @Value("${security.jwt.secret:change-me-secret-key-change-me-secret-key-change-me-secret-key}")
    private String jwtSecret;

    @Value("${security.jwt.expiration-ms:3600000}")
    private long tokenExpirationMs;

    /**
     * Add a token to the blacklist (cache)
     * The cache automatically expires entries based on TTL
     * TTL is calculated from token expiration time
     */
    public void blacklistToken(String token) {
        try {
            log.debug("[BLACKLIST] Attempting to blacklist token: {}...", token.substring(0, Math.min(20, token.length())));
            long expirationTime = getTokenExpirationTime(token);
            long currentTime = System.currentTimeMillis();
            long ttlMs = expirationTime - currentTime;


            if (ttlMs > 0) {
                // Put token into cache with TTL
                Cache cache = cacheManager.getCache("tokenBlacklist");
                if (cache != null) {
                    cache.put(token, "blacklisted_at_" + System.currentTimeMillis());
                        token.substring(0, Math.min(20, token.length())), ttlMs / 1000);
                }
            }
        } catch (Exception e) {
            log.error("[BLACKLIST ERROR] Failed to blacklist token: {}", e.getMessage(), e);
        }
    }

    /**
     * Check if a token is blacklisted
     * Returns true if token is in blacklist (meaning it's been revoked)
     * Returns false if token is NOT in blacklist (still valid)
     */
    public boolean isTokenBlacklisted(String token) {
        try {

            Cache cache = cacheManager.getCache("tokenBlacklist");

            if (cache == null) {
                return true;
            }


            // Check if token exists in cache
            Object cachedValue = cache.get(token);
            if (cachedValue != null) {
                return true;
            } else {
               return false;
            }
        } catch (Exception e) {
            log.error("[BLACKLIST CHECK ERROR] Failed to check blacklist: {}", e.getMessage(), e);
            return true;
        }
    }

    /**
     * Extract expiration time from JWT token
     */
    private long getTokenExpirationTime(String token) {
        try {
            byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
            SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);

            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date expiration = claims.getExpiration();
            if (expiration == null) {
                throw new IllegalArgumentException("Token has no expiration claim");
            }

            return expiration.getTime();
        } catch (Exception e) {
            log.error("[TOKEN PARSING ERROR] Failed to parse token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid token format", e);
        }
    }


}



