package com.bizflow.backend.dto;

public class DashboardResponse {

    private long totalProjects;
    private long totalTasks;
    private long todoTasks;
    private long inProgressTasks;
    private long inReviewTasks;
    private long completedTasks;
    private long cancelledTasks;
    private long myTasks;

    public DashboardResponse() {
    }

    public DashboardResponse(
            long totalProjects,
            long totalTasks,
            long todoTasks,
            long inProgressTasks,
            long inReviewTasks,
            long completedTasks,
            long cancelledTasks,
            long myTasks) {

        this.totalProjects = totalProjects;
        this.totalTasks = totalTasks;
        this.todoTasks = todoTasks;
        this.inProgressTasks = inProgressTasks;
        this.inReviewTasks = inReviewTasks;
        this.completedTasks = completedTasks;
        this.cancelledTasks = cancelledTasks;
        this.myTasks = myTasks;
    }

    public long getTotalProjects() {
        return totalProjects;
    }

    public long getTotalTasks() {
        return totalTasks;
    }

    public long getTodoTasks() {
        return todoTasks;
    }

    public long getInProgressTasks() {
        return inProgressTasks;
    }

    public long getInReviewTasks() {
        return inReviewTasks;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public long getCancelledTasks() {
        return cancelledTasks;
    }

    public long getMyTasks() {
        return myTasks;
    }
}