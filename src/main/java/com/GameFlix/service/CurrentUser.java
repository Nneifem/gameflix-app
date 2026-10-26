package com.GameFlix.service;

import com.GameFlix.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CurrentUser {
    private final UserService userService;

    public CurrentUser(UserService userService) {
        this.userService = userService;
    }

    public User from(Authentication auth) {
        return userService.findByEmail((String) auth.getPrincipal())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
