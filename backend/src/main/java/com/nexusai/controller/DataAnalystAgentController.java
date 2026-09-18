package com.nexusai.controller;

import com.nexusai.agent.DataAnalystAgent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/agents/data-analyst")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://127.0.0.1:5173"
})
public class DataAnalystAgentController {

    private final DataAnalystAgent dataAnalystAgent;

    public DataAnalystAgentController(
            DataAnalystAgent dataAnalystAgent
    ) {
        this.dataAnalystAgent = dataAnalystAgent;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> analyze(
            @RequestBody Map<String, String> request
    ) {

        String task = request.get("task");

        String result = dataAnalystAgent.analyze(task);

        return ResponseEntity.ok(
                Map.of(
                        "agent", "Data Analyst Agent",
                        "result", result
                )
        );
    }
}