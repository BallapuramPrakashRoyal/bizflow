package com.bizflow.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import com.bizflow.backend.exception.ResourceNotFoundException;
import com.bizflow.backend.exception.ForbiddenException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(
            BadCredentialsException exception) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid email or password"));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<Map<String, String>> handleResourceNotFound(
        ResourceNotFoundException ex) {

    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage());

    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(error);
}
@ExceptionHandler(ForbiddenException.class)
public ResponseEntity<Map<String, String>> handleForbidden(
        ForbiddenException ex) {

    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage());

    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(error);
}

}