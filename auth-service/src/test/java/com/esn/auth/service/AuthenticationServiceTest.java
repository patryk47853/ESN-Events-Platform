package com.esn.auth.service;

import com.esn.auth.dto.AuthenticationRequest;
import com.esn.auth.dto.AuthenticationResponse;
import com.esn.auth.dto.RegisterRequest;
import com.esn.auth.entity.Role;
import com.esn.auth.entity.User;
import com.esn.auth.exception.EmailAlreadyExistsException;
import com.esn.auth.repository.UserRepository;
import com.esn.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("  PATRYK@TEST.COM  ");
        registerRequest.setPassword("password123");
    }

    @Test
    void shouldRegisterUserSuccessfully() {

        when(userRepository.existsByEmail("patryk@test.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        when(jwtService.generateToken(any(User.class)))
                .thenReturn("generated-jwt");

        AuthenticationResponse response = authenticationService.register(registerRequest);

        assertNotNull(response);
        assertEquals("generated-jwt", response.getToken());

        verify(userRepository)
                .existsByEmail("patryk@test.com");

        verify(passwordEncoder)
                .encode("password123");

        verify(userRepository).save(argThat(user ->
                user.getEmail().equals("patryk@test.com")
                        && user.getPassword().equals("encoded-password")
                        && user.getRole() == Role.USER
        ));

        verify(jwtService).generateToken(any(User.class));
    }

    @Test
    void shouldRejectDuplicateEmail() {

        when(userRepository.existsByEmail("patryk@test.com"))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> authenticationService.register(registerRequest)
        );

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {

        AuthenticationRequest request = new AuthenticationRequest();

        request.setEmail("  PATRYK@TEST.COM ");
        request.setPassword("password123");

        User user = User.builder()
                .id(1L)
                .email("patryk@test.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail("patryk@test.com"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(user))
                .thenReturn("generated-jwt");

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertEquals("generated-jwt", response.getToken());

        verify(authenticationManager).authenticate(
                argThat(authentication ->
                        authentication.getPrincipal()
                                .equals("patryk@test.com")
                                && authentication.getCredentials()
                                .equals("password123")
                )
        );

        verify(jwtService).generateToken(user);
    }

    @Test
    void shouldNotGenerateTokenWhenAuthenticationFails() {

        AuthenticationRequest request = new AuthenticationRequest();

        request.setEmail("patryk@test.com");
        request.setPassword("wrong-password");

        doThrow(
                new org.springframework.security
                        .authentication.BadCredentialsException(
                        "Bad credentials"
                )
        ).when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(
                org.springframework.security
                        .authentication.BadCredentialsException.class,
                () -> authenticationService.authenticate(request)
        );

        verify(userRepository, never()).findByEmail(anyString());
        verify(jwtService, never()).generateToken(any());
    }
}