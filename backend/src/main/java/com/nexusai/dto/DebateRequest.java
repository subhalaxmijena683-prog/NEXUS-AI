package com.nexusai.dto;

public class DebateRequest {

    private String topic;
    private String userArgument;

    public DebateRequest() {
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUserArgument() {
        return userArgument;
    }

    public void setUserArgument(String userArgument) {
        this.userArgument = userArgument;
    }
}