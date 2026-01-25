package com.taskflow.api.service;

import com.taskflow.api.domain.*;
import com.taskflow.api.dto.*;
import com.taskflow.api.repository.UserRepository;
import com.taskflow.api.security.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, AuthenticationManager authManager, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwt = jwt;
    }

    public AuthResponse register(RegisterRequest req) {
        if (users.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        String hash = encoder.encode(req.password());
        User user = new User(req.email(), hash, UserRole.USER, Instant.now());
        users.save(user);

        return new AuthResponse(jwt.generateToken(user.getEmail()));
    }

    public AuthResponse login(LoginRequest req) {
        var token = new UsernamePasswordAuthenticationToken(req.email(), req.password());
        authManager.authenticate(token); // throws if invalid

        return new AuthResponse(jwt.generateToken(req.email()));
    }
}
