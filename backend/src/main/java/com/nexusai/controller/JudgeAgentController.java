package com.nexusai.controller;

import com.nexusai.agent.JudgeAgent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/agents")
public class JudgeAgentController {

    private final JudgeAgent agent;

    public JudgeAgentController(
            JudgeAgent agent
    ) {
        this.agent = agent;
    }

    @PostMapping("/judge")
    public ResponseEntity<?> judge(
            @RequestBody Map<String, String> request
    ) {

        String problem = request.get("problem");
        String decision = request.get("decision");
        String solution = request.get("solution");
        String prediction = request.get("prediction");

        if (problem == null || problem.isBlank()
                || decision == null || decision.isBlank()
                || solution == null || solution.isBlank()
                || prediction == null || prediction.isBlank()) {

            return ResponseEntity.badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    "problem, decision, solution and prediction are required"
                            )
                    );
        }

        return ResponseEntity.ok(
                Map.of(
                        "agent", "Judge Agent",
                        "result",
                        agent.evaluate(
                                problem,
                                decision,
                                solution,
                                prediction
                        )
                )
        );
    }
}
