package com.esn.auth.security;

import com.esn.auth.entity.Role;
import com.esn.auth.entity.User;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {

        String secret = Base64.getEncoder()
                .encodeToString(
                        "01234567890123456789012345678901"
                                .getBytes(StandardCharsets.UTF_8)
                );

        jwtService = new JwtService(secret, 3_600_000L);

        user = User.builder()
                .id(1L)
                .email("patryk@test.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();
    }

    @Test
    void shouldGenerateToken() {

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void shouldExtractUsernameFromToken() {

        String token = jwtService.generateToken(user);

        String username = jwtService.extractUsername(token);

        assertEquals("patryk@test.com", username);
    }

    @Test
    void shouldValidateCorrectToken() {

        String token = jwtService.generateToken(user);

        boolean valid = jwtService.isTokenValid(token, user);

        assertTrue(valid);
    }

    @Test
    void shouldRejectTokenForDifferentUser() {

        String token = jwtService.generateToken(user);

        User differentUser = User.builder()
                .id(2L)
                .email("different@test.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        boolean valid = jwtService.isTokenValid(
                token,
                differentUser
        );

        assertFalse(valid);
    }

    @Test
    void shouldRejectModifiedToken() {

        String token = jwtService.generateToken(user);

        String modifiedToken = token.substring(0, token.length() - 1) + "x";

        assertThrows(
                JwtException.class,
                () -> jwtService.extractUsername(modifiedToken)
        );
    }
}