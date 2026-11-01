package com.bizflow.backend.dto;

import com.bizflow.backend.entity.WorkflowStageStatus;
import jakarta.validation.constraints.NotNull;

public class WorkflowStageStatusRequest {

    @NotNull(message = "Workflow stage status is required")
    private WorkflowStageStatus status;

    public WorkflowStageStatusRequest() {
    }

    public WorkflowStageStatus getStatus() { return status; }
    public void setStatus(WorkflowStageStatus status) { this.status = status; }
}
