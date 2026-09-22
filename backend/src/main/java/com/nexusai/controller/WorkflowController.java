package com.nexusai.controller;

import com.nexusai.service.WorkflowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(
            WorkflowService workflowService
    ) {
        this.workflowService = workflowService;
    }

    @PostMapping("/execute")
    public ResponseEntity<?> execute(
            @RequestBody Map<String, String> request
    ) {

        String problem = request.get("problem");

        if (problem == null || problem.isBlank()) {

            return ResponseEntity.badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    "Problem is required"
                            )
                    );
        }

        try {

            return ResponseEntity.ok(
                    Map.of(
                            "workflow",
                            "NEXUS AI Autonomous Enterprise Workflow",
                            "result",
                            workflowService.execute(problem)
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }
}
