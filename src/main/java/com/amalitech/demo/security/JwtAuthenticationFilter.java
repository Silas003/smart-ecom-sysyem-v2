package com.amalitech.demo.security;

import com.amalitech.demo.dto.TokenValidationResult;
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

        String token = JwtUtil.extractTokenFromRequest(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                throw new BadCredentialsException("Token has been revoked");
            }

            TokenValidationResult validationResult = jwtService.validateAndExtract(token);

            if(!validationResult.isValid()){
                throw new BadCredentialsException("Invalid or expired token");
            }

            List<SimpleGrantedAuthority> authorities = validationResult.getRoles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(validationResult.getSubject(), null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new BadCredentialsException("Authentication failed: " + e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}




