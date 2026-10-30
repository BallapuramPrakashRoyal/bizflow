package com.bizflow.backend.controller;

import com.bizflow.backend.dto.UserResponse;
import com.bizflow.backend.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        return userService.getCurrentUser(authentication.getName());
    }

    @GetMapping("/lookup")
    public UserResponse findUserByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email);
    }
}
