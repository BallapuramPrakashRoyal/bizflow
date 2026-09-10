package com.bizflow.backend.service;

import com.bizflow.backend.entity.User;
import com.bizflow.backend.exception.ResourceNotFoundException;
import com.bizflow.backend.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser(Authentication authentication) {

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    public Long getCurrentUserId(Authentication authentication) {
        return getCurrentUser(authentication).getId();
    }
}