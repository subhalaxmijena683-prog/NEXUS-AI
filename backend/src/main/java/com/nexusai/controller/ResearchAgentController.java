package com.nexusai.controller;

import com.nexusai.agent.ResearchAgent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/agents/research")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://127.0.0.1:5173"
})
public class ResearchAgentController {

    private final ResearchAgent researchAgent;

    public ResearchAgentController(
            ResearchAgent researchAgent
    ) {
        this.researchAgent = researchAgent;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> research(
            @RequestBody Map<String, String> request
    ) {

        String task = request.get("task");

        String result = researchAgent.research(task);

        return ResponseEntity.ok(
                Map.of(
                        "agent", "Research Agent",
                        "result", result
                )
        );
    }
}