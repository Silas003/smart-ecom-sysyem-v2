package com.amalitech.demo.security;

import com.amalitech.demo.dto.UserRole;
import com.amalitech.demo.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;
    private final String issuer;

    public JwtService(
            @Value("${security.jwt.secret:change-me-secret-key-change-me-secret-key-change-me-secret-key}") String secret,
            @Value("${security.jwt.expiration-ms:3600000}") long expirationMs,
            @Value("${security.jwt.issuer:demo-app}") String issuer
    ) {
        // Treat the secret as a plain text value, not Base64, to avoid decoding errors
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
        this.issuer = issuer;
    }

    public String generateToken(User user) {
        String subject = user.getEmail();
        UserRole role = user.getUserRole();
        List<String> roles = role != null ? List.of(role.name()) : List.of();

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(subject)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiry)
                .claims(Map.of("roles", roles))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractSubject(String token) {
        return getAllClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Object rolesClaim = getAllClaims(token).get("roles");
        if (rolesClaim instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }

    public boolean isTokenValid(String token, String expectedSubject) {
        Claims claims = getAllClaims(token);
        String subject = claims.getSubject();
        Date expiration = claims.getExpiration();
        return subject != null
                && subject.equals(expectedSubject)
                && expiration != null
                && expiration.after(new Date());
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
