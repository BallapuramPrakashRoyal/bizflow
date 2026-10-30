package com.bizflow.backend.controller;

import com.bizflow.backend.dto.LoginRequest;
import com.bizflow.backend.dto.LoginResponse;
import com.bizflow.backend.dto.UserRequest;
import com.bizflow.backend.dto.UserResponse;
import com.bizflow.backend.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(
            @Valid @RequestBody UserRequest request) {

        return userService.createUser(request);
    }
}
