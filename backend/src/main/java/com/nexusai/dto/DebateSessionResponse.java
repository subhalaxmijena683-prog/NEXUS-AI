package com.nexusai.dto;

import java.time.LocalDateTime;

public class DebateSessionResponse {

    private Long id;
    private String topic;
    private String position;
    private LocalDateTime createdAt;

    public DebateSessionResponse(
            Long id,
            String topic,
            String position,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.topic = topic;
        this.position = position;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public String getPosition() {
        return position;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}