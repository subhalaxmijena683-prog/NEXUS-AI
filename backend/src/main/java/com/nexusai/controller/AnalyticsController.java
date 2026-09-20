package com.nexusai.controller;

import com.nexusai.entity.AgentExecution;
import com.nexusai.repository.AgentExecutionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://127.0.0.1:5173"
})
public class AnalyticsController {

    private final AgentExecutionRepository repository;

    public AnalyticsController(
            AgentExecutionRepository repository
    ) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<?> getAnalytics() {

        List<AgentExecution> executions =
                repository.findAll();

        long totalExecutions = executions.size();

        long successfulExecutions =
                executions.stream()
                        .filter(e ->
                                "SUCCESS".equals(e.getStatus())
                        )
                        .count();

        long failedExecutions =
                executions.stream()
                        .filter(e ->
                                "FAILED".equals(e.getStatus())
                        )
                        .count();

        double averageDuration = executions.stream()
                .mapToLong(AgentExecution::getDurationMs)
                .average()
                .orElse(0.0);

        Map<String, Object> analytics =
                new HashMap<>();

        analytics.put(
                "totalExecutions",
                totalExecutions
        );

        analytics.put(
                "successfulExecutions",
                successfulExecutions
        );

        analytics.put(
                "failedExecutions",
                failedExecutions
        );

        analytics.put(
                "averageDurationMs",
                Math.round(averageDuration)
        );

        analytics.put(
                "executions",
                executions
        );

        return ResponseEntity.ok(analytics);
    }
}
