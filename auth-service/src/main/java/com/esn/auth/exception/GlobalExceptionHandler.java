package com.esn.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleBadCredentials(BadCredentialsException exception) {
        return Map.of(
                "message", "Invalid email or password",
                "timestamp", LocalDateTime.now()
        );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleEmailAlreadyExists(EmailAlreadyExistsException exception) {
        return Map.of(
                "message", exception.getMessage(),
                "timestamp", LocalDateTime.now()
        );
    }
}