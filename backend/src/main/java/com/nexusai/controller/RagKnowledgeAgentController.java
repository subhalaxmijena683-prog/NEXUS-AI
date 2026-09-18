package com.nexusai.controller;

import com.nexusai.agent.RagKnowledgeAgent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/agents/rag")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://127.0.0.1:5173"
})
public class RagKnowledgeAgentController {

    private final RagKnowledgeAgent ragKnowledgeAgent;

    public RagKnowledgeAgentController(
            RagKnowledgeAgent ragKnowledgeAgent
    ) {
        this.ragKnowledgeAgent = ragKnowledgeAgent;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> ask(
            @RequestBody Map<String, String> request
    ) {

        String question = request.get("question");

        String result =
                ragKnowledgeAgent.ask(question);

        return ResponseEntity.ok(
                Map.of(
                        "agent",
                        "RAG Knowledge Agent",
                        "result",
                        result
                )
        );
    }
}