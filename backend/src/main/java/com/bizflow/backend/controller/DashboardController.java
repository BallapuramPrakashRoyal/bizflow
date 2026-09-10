package com.bizflow.backend.controller;

import com.bizflow.backend.dto.DashboardResponse;
import com.bizflow.backend.service.CurrentUserService;
import com.bizflow.backend.service.DashboardService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final CurrentUserService currentUserService;

    public DashboardController(
            DashboardService dashboardService,
            CurrentUserService currentUserService) {

        this.dashboardService = dashboardService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public DashboardResponse getDashboard(
            Authentication authentication) {

        Long currentUserId =
                currentUserService.getCurrentUserId(authentication);

        return dashboardService.getDashboard(currentUserId);
    }
}