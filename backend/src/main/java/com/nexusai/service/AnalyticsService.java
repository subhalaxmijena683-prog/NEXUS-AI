package com.nexusai.service;

import com.nexusai.entity.AgentExecution;
import com.nexusai.repository.AgentExecutionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AnalyticsService {

    private final AgentExecutionRepository repository;

    public AnalyticsService(AgentExecutionRepository repository) {
        this.repository = repository;
    }

    public void recordSuccess(
            String agentName,
            LocalDateTime startedAt,
            long durationMs
    ) {
        AgentExecution execution = new AgentExecution(
                agentName,
                "SUCCESS",
                durationMs,
                startedAt,
                LocalDateTime.now(),
                null
        );

        repository.save(execution);
    }

    public void recordFailure(
            String agentName,
            LocalDateTime startedAt,
            long durationMs,
            String errorMessage
    ) {
        AgentExecution execution = new AgentExecution(
                agentName,
                "FAILED",
                durationMs,
                startedAt,
                LocalDateTime.now(),
                errorMessage
        );

        repository.save(execution);
    }
}