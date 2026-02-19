package com.amalitech.demo.services;

import com.amalitech.demo.dto.UserRole;
import com.amalitech.demo.models.User;
import com.amalitech.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class CustomOidcUserService extends OidcUserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOidcUserService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        logger.info("=== CustomOidcUserService.loadUser() called ===");

        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getAttribute("email");
        String name = oidcUser.getAttribute("name");
        logger.info("User email: {}", email);

        // Find or create user in database
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    logger.info("Creating new user with email: {}", email);
                    User newUser = new User();
                    newUser.setEmail(email);
                    if (name != null && !name.isEmpty()) {
                        newUser.setUsername(name);
                    } else {
                        // Fallback: use email prefix as username
                        newUser.setUsername(email.split("@")[0]);
                    }

                    // Set a random password for OAuth2 users (they won't use it)
                    newUser.setPassword(UUID.randomUUID().toString());
                    newUser.setUserRole(UserRole.customer);
                    return userRepository.save(newUser);
                });

        logger.info("User role from database: {}", user.getUserRole());

        // Add role from database to authorities with ROLE_ prefix
        Set<GrantedAuthority> authorities = new HashSet<>(oidcUser.getAuthorities());
        String roleWithPrefix = "ROLE_" + user.getUserRole().name();
        authorities.add(new SimpleGrantedAuthority(roleWithPrefix));

        logger.info("Added authority: {}", roleWithPrefix);
        logger.info("All authorities: {}", authorities);

        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }
}
