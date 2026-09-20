package com.nexusai.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agent_executions")
public class AgentExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String agentName;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Long durationMs;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime completedAt;

    @Column(length = 2000)
    private String errorMessage;

    public AgentExecution() {
    }

    public AgentExecution(
            String agentName,
            String status,
            Long durationMs,
            LocalDateTime startedAt,
            LocalDateTime completedAt,
            String errorMessage
    ) {
        this.agentName = agentName;
        this.status = status;
        this.durationMs = durationMs;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.errorMessage = errorMessage;
    }

    public Long getId() {
        return id;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}