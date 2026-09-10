package com.bizflow.backend.controller;

import com.bizflow.backend.dto.CommentRequest;
import com.bizflow.backend.dto.CommentResponse;
import com.bizflow.backend.service.CommentService;
import com.bizflow.backend.service.CurrentUserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final CurrentUserService currentUserService;

    public CommentController(
            CommentService commentService,
            CurrentUserService currentUserService) {

        this.commentService = commentService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {

        Long currentUserId =
                currentUserService.getCurrentUserId(authentication);

        return commentService.createComment(
                request,
                currentUserId
        );
    }

    @GetMapping("/task/{taskId}")
    public List<CommentResponse> getCommentsByTaskId(
            @PathVariable Long taskId,
            Authentication authentication) {

        Long currentUserId =
                currentUserService.getCurrentUserId(authentication);

        return commentService.getCommentsByTaskId(
                taskId,
                currentUserId
        );
    }
}