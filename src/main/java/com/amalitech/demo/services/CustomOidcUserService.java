package com.amalitech.demo.services;

import com.amalitech.demo.dto.Provider;
import com.amalitech.demo.dto.UserRole;
import com.amalitech.demo.models.User;
import com.amalitech.demo.repository.UserRepository;
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
    private final UserRepository userRepository;

    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getAttribute("email");
        String name = oidcUser.getAttribute("name");


        User user = userRepository.findByEmail(email).orElseGet(() -> {

            User newUser = new User();
            newUser.setEmail(email);
            if (name != null && !name.isEmpty()) {
                newUser.setUsername(name);
            } else {
                newUser.setUsername(email.split("@")[0]);
            }

            newUser.setPassword(UUID.randomUUID().toString());
            newUser.setUserRole(UserRole.customer);
            newUser.setProvider(Provider.google);

            User savedUser = userRepository.save(newUser);
            return savedUser;
        });


        Set<GrantedAuthority> authorities = new HashSet<>(oidcUser.getAuthorities());
        String roleWithPrefix = "ROLE_" + user.getUserRole().name();
        authorities.add(new SimpleGrantedAuthority(roleWithPrefix));


        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }
}


