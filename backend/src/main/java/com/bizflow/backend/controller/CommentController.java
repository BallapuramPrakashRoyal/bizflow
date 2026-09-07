package com.bizflow.backend.controller;

import com.bizflow.backend.dto.CommentRequest;
import com.bizflow.backend.dto.CommentResponse;
import com.bizflow.backend.entity.User;
import com.bizflow.backend.repository.UserRepository;
import com.bizflow.backend.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;

    public CommentController(
            CommentService commentService,
            UserRepository userRepository) {

        this.commentService = commentService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return commentService.createComment(
                request,
                user.getId()
        );
    }

    @GetMapping("/task/{taskId}")
    public List<CommentResponse> getCommentsByTaskId(
            @PathVariable Long taskId) {

        return commentService.getCommentsByTaskId(taskId);
    }
}