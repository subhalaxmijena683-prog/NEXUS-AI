package com.nexusai.backend.workflow;

public class WorkflowResponse {

    private String workflow;
    private String result;

    public WorkflowResponse() {
    }

    public WorkflowResponse(String workflow, String result) {
        this.workflow = workflow;
        this.result = result;
    }

    public String getWorkflow() {
        return workflow;
    }

    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}