package com.nexusai.controller;

import com.nexusai.agent.SolutionAgent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/agents")
public class SolutionAgentController {

    private final SolutionAgent agent;

    public SolutionAgentController(
            SolutionAgent agent
    ) {
        this.agent = agent;
    }

    @PostMapping("/solution")
    public ResponseEntity<?> solve(
            @RequestBody Map<String, String> request
    ) {

        String task = request.get("task");

        if (task == null || task.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Task is required"));
        }

        return ResponseEntity.ok(
                Map.of(
                        "agent", "Solution Agent",
                        "result", agent.designSolution(task)
                )
        );
    }
}
