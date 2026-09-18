package com.nexusai.backend.workflow;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/execute")
    public ResponseEntity<WorkflowResponse> executeWorkflow(
            @RequestBody WorkflowRequest request) {

        WorkflowResponse response =
                workflowService.executeWorkflow(request.getProblem());

        return ResponseEntity.ok(response);
    }
}