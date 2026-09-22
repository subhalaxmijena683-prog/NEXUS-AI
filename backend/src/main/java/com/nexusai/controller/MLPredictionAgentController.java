package com.nexusai.controller;

import com.nexusai.agent.MLPredictionAgent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/agents")
public class MLPredictionAgentController {

    private final MLPredictionAgent agent;

    public MLPredictionAgentController(
            MLPredictionAgent agent
    ) {
        this.agent = agent;
    }

    @PostMapping("/ml-prediction")
    public ResponseEntity<?> predict(
            @RequestBody Map<String, String> request
    ) {

        String task = request.get("task");

        if (task == null || task.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Task is required"));
        }

        return ResponseEntity.ok(
                Map.of(
                        "agent", "ML Prediction Agent",
                        "result", agent.predict(task)
                )
        );
    }
}
