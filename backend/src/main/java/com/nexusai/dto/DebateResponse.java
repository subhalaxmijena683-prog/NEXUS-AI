package com.nexusai.dto;

import java.time.LocalDateTime;

public class DebateResponse {

    private Long id;
    private String topic;
    private String userArgument;
    private String aiArgument;
    private Double score;
    private LocalDateTime createdAt;

    public DebateResponse(
            Long id,
            String topic,
            String userArgument,
            String aiArgument,
            Double score,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.topic = topic;
        this.userArgument = userArgument;
        this.aiArgument = aiArgument;
        this.score = score;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public String getUserArgument() {
        return userArgument;
    }

    public String getAiArgument() {
        return aiArgument;
    }

    public Double getScore() {
        return score;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}