package com.nexusai.dto;

public class CreateDebateSessionRequest {

    private String topic;
    private String position;

    public CreateDebateSessionRequest() {
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}