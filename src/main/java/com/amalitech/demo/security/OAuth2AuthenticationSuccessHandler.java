package com.amalitech.demo.security;

import com.amalitech.demo.models.User;
import com.amalitech.demo.repository.UserRepository;
import com.amalitech.demo.services.interfaces.UserServiceInterface;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceInterface userService;

    @Value("${app.oauth2.redirect-uri:http://localhost:3000/oauth2/redirect}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("[OAUTH2] Authentication success handler triggered");

        try {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            String email = oidcUser.getAttribute("email");
            log.info("[OAUTH2] Processing OAuth2 user: {}", email);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.error("[OAUTH2] User not found after OAuth2 login for email: {}", email);
                        return new RuntimeException("User not found after OAuth2 login");
                    });

            log.info("[OAUTH2] User found: {}, generating tokens", email);

            Map<String, String> jwtToken = jwtService.generateToken(user);
            String accessToken = jwtToken.get("access");
            String refreshToken = jwtToken.get("refresh");

            userService.setCookie(refreshToken, response);
            log.info("[OAUTH2] Refresh token set in secure cookie for user: {}", email);

            // Redirect to frontend with ONLY access token in query param (short-lived, safe)
            String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("access", accessToken)
                    .build()
                    .toUriString();

            log.info("[OAUTH2] Redirecting to: {}", redirectUri);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            log.error("[OAUTH2] OAuth2 authentication success handler failed: {}", e.getMessage(), e);
            throw new ServletException("OAuth2 authentication failed", e);
        }
    }
}

