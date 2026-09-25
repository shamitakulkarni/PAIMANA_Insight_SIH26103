package com.paimana.insight.service;

import com.paimana.insight.dto.DTOs.*;
import com.paimana.insight.model.User;
import com.paimana.insight.model.Role;
import com.paimana.insight.repository.UserRepository;
import com.paimana.insight.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder enc;
    private final JwtService jwt;

    public AuthService(
            UserRepository u,
            PasswordEncoder e,
            JwtService j
    ) {
        users = u;
        enc = e;
        jwt = j;
    }

    // LOGIN
    public LoginOut login(Login x) {

        User u = users.findByEmail(x.email())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password"
                        )
                );

        if (!enc.matches(
                x.password(),
                u.getPasswordHash()
        )) {
            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }

        return new LoginOut(
                jwt.generate(u),
                u.getName(),
                u.getEmail(),
                u.getRole().name()
        );
    }

    // SIGNUP
    public LoginOut signup(Signup x) {

        if (users.findByEmail(x.email()).isPresent()) {
            throw new IllegalArgumentException(
                    "Email already registered"
            );
        }

        User u = new User();

        u.setName(x.name());
        u.setEmail(x.email());

        // Encrypt password
        u.setPasswordHash(
                enc.encode(x.password())
        );

        // Normal signup users
        u.setRole(Role.MONITORING);

        users.save(u);

        // Automatically log in after signup
        return new LoginOut(
                jwt.generate(u),
                u.getName(),
                u.getEmail(),
                u.getRole().name()
        );
    }
}