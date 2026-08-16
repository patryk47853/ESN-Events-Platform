package com.esn.auth.service;

import com.esn.auth.dto.AuthenticationRequest;
import com.esn.auth.dto.AuthenticationResponse;
import com.esn.auth.dto.RegisterRequest;
import com.esn.auth.entity.Role;
import com.esn.auth.entity.User;
import com.esn.auth.exception.EmailAlreadyExistsException;
import com.esn.auth.repository.UserRepository;
import com.esn.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {

        String normalizedEmail = request.getEmail()
                .trim()
                .toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException(normalizedEmail);
        }

        User user = User.builder()
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        return AuthenticationResponse.builder()
                .token(jwtService.generateToken(savedUser))
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        String normalizedEmail = request.getEmail()
                .trim()
                .toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        normalizedEmail,
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow();

        return AuthenticationResponse.builder()
                .token(jwtService.generateToken(user))
                .build();
    }
}