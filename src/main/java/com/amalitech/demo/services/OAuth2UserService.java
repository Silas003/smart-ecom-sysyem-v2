package com.amalitech.demo.services;

import com.amalitech.demo.dto.UserRole;
import com.amalitech.demo.models.User;
import com.amalitech.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {


    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");

        // Find or create user in database
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUserRole(UserRole.customer);
                    return userRepository.save(newUser);
                });

        Set<GrantedAuthority> authorities = new HashSet<>(oauth2User.getAuthorities());
        String roleWithPrefix = "ROLE_" + user.getUserRole().name();
        authorities.add(new SimpleGrantedAuthority(roleWithPrefix));

        DefaultOAuth2User result = new DefaultOAuth2User(authorities, oauth2User.getAttributes(), "email");
        return result;
    }
}