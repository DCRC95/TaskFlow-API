package com.taskflow.api.service;

import java.time.Instant;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taskflow.api.domain.User;
import com.taskflow.api.domain.UserRole;
import com.taskflow.api.dto.AuthResponse;
import com.taskflow.api.dto.LoginRequest;
import com.taskflow.api.dto.RegisterRequest;
import com.taskflow.api.exception.BadRequestException;
import com.taskflow.api.repository.UserRepository;
import com.taskflow.api.security.JwtService;

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
            throw new BadRequestException("Email already in use");
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
