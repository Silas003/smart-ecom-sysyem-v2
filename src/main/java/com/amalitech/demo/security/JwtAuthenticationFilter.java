package com.amalitech.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter
 * Validates JWT tokens and extracts claims (roles, email) WITHOUT database queries
 * - No token: Let request continue (OAuth2 may handle)
 * - Invalid/expired token: Throw exception (401 response)
 * - Valid token: Extract roles from claims and set authentication
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            String email = jwtService.extractSubject(token);

            if (email == null) {
                throw new BadCredentialsException("Invalid token: cannot extract subject");
            }

            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                throw new BadCredentialsException("Token has been revoked");
            }

            if (!jwtService.isTokenValid(token, email)) {
                throw new BadCredentialsException("Token expired or invalid");
            }

            // Extract roles from JWT claims (NO DATABASE QUERY)
            List<String> rolesFromToken = jwtService.extractRolesFromToken(token);
            if (rolesFromToken == null || rolesFromToken.isEmpty()) {
                log.warn("No roles found in token for user: {}", email);
                rolesFromToken = List.of("ROLE_USER");
            }

            List<SimpleGrantedAuthority> authorities = rolesFromToken.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new BadCredentialsException("Authentication failed: " + e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}




