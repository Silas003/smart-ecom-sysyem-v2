package com.amalitech.demo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {

    private CurrentUser() {}

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static String getEmail() {
        Authentication auth = getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}

