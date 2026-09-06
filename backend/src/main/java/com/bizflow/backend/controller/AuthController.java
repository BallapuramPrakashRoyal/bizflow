package com.bizflow.backend.controller;

import com.bizflow.backend.dto.LoginRequest;
import com.bizflow.backend.dto.LoginResponse;
import com.bizflow.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }
}