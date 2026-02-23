package com.amalitech.demo.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.rmi.ServerException;

@Component
public class oauth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull AuthenticationException exception) throws java.io.IOException, ServerException {
        String errorMessage = request.getParameter("error");
        if (errorMessage != null) {
            logger.error("OAuth2 authentication failed: " + errorMessage);
        } else {
            logger.error("OAuth2 authentication failed with unknown error");
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
