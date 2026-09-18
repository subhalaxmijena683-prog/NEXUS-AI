package com.nexusai.backend.workflow;

public class WorkflowRequest {

    private String problem;

    public WorkflowRequest() {
    }

    public WorkflowRequest(String problem) {
        this.problem = problem;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }
}