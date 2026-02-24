package com.amalitech.demo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class oauth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String errorCode = request.getParameter("error");
        String errorDescription = request.getParameter("error_description");
        String errorUri = request.getParameter("error_uri");

        log.error("[OAUTH2 FAILURE] OAuth2 authentication failed - Code: {}, Description: {}, Exception: {}",
                errorCode, errorDescription, exception.getMessage());
        log.debug("[OAUTH2 FAILURE] Error URI: {}", errorUri);

        // Set response status and content type
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Create error response
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("statusCode", HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.put("error", "OAUTH2_AUTHENTICATION_FAILED");
        errorResponse.put("message", "OAuth2 authentication failed");
        errorResponse.put("details", exception.getMessage());
        errorResponse.put("errorCode", errorCode);
        errorResponse.put("timestamp", LocalDateTime.now().toString());

        // Write JSON response
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), errorResponse);
        } catch (IOException e) {
            log.error("[OAUTH2 FAILURE] Failed to write error response: {}", e.getMessage(), e);
        }
    }
}

