package com.bizflow.backend.service;

import com.bizflow.backend.dto.UserRequest;
import com.bizflow.backend.dto.UserResponse;
import com.bizflow.backend.entity.User;
import com.bizflow.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.bizflow.backend.dto.LoginRequest;
import com.bizflow.backend.dto.LoginResponse;
import com.bizflow.backend.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService) {

    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
}

    public UserResponse createUser(UserRequest request) {

        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getName().trim(),
                email,
                hashedPassword
        );

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCreatedAt()
        );
    }
    public LoginResponse login(LoginRequest request) {

    User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase(Locale.ROOT))
            .orElseThrow(() ->
                    new BadCredentialsException("Invalid email or password"));

    if (!passwordEncoder.matches(
            request.getPassword(),
            user.getPassword())) {

        throw new BadCredentialsException("Invalid email or password");
    }

    String token = jwtService.generateToken(user.getEmail());

    return new LoginResponse(token);
}
public UserResponse getCurrentUser(String email) {

    User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new com.bizflow.backend.exception.ResourceNotFoundException("User not found"));

    return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getCreatedAt()
    );
}

public UserResponse getUserByEmail(String email) {

    User user = userRepository.findByEmail(email.trim().toLowerCase(Locale.ROOT))
            .orElseThrow(() ->
                    new com.bizflow.backend.exception.ResourceNotFoundException("User not found"));

    return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getCreatedAt()
    );
}

}
