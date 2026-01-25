package com.taskflow.api.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    public String email() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName(); // this is the username (we set it to email)
    }
}
