package com.amalitech.demo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles authentication errors for JWT tokens.
 *
 * Only used when:
 * - A JWT token was provided AND
 * - The token was INVALID/EXPIRED/BLACKLISTED (BadCredentialsException thrown by filter)
 *
 * When no token is provided, this entry point is NOT used.
 * Instead, OAuth2 login endpoint is offered.
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {

        log.error("[JWT ENTRY POINT] Authentication failed: {}", authException.getMessage());

        // Check if this is a BadCredentialsException from our JWT filter (invalid token)
        // vs a generic authentication exception (no token)
        boolean isJwtError = authException instanceof BadCredentialsException
                            && authException.getMessage() != null
                            && (authException.getMessage().contains("Token")
                                || authException.getMessage().contains("token")
                                || authException.getMessage().contains("Authentication failed"));

        if (isJwtError) {
            sendJsonError(response, authException);
        } else {
            log.debug("[JWT ENTRY POINT] Not a JWT error, delegating to default handling");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
        }
    }


    private void sendJsonError(HttpServletResponse response, AuthenticationException authException)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = new HashMap<>();
        body.put("statusCode", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("message", "Unauthorized - Invalid or expired JWT token");
        body.put("error", "UNAUTHORIZED");
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("details", authException.getMessage());

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}


