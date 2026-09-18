package com.nexusai.agent;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class DataAnalystAgent {

    private final RestTemplate restTemplate;

    public DataAnalystAgent() {
        this.restTemplate = new RestTemplate();
    }

    public String analyze(String task) {

        if (task == null || task.isBlank()) {
            throw new IllegalArgumentException(
                    "Data analysis task cannot be empty"
            );
        }

        String prompt = """
                You are the NEXUS AI Data Analyst Agent.

                Your responsibility is to analyze business,
                operational, financial, or enterprise data
                and convert it into useful insights.

                Follow these steps:

                1. Understand the analysis request.
                2. Identify the important metrics and variables.
                3. Analyze the provided information.
                4. Identify patterns and trends.
                5. Identify risks or anomalies.
                6. Provide actionable business insights.
                7. Clearly mention when actual data is missing.

                IMPORTANT:
                Do not invent real data.
                If the user has not provided numerical data,
                perform conceptual analysis and clearly state
                that actual data is required for quantitative
                conclusions.

                Give the result in this format:

                ANALYSIS OBJECTIVE:
                <objective>

                KEY METRICS:
                - metric 1
                - metric 2
                - metric 3

                KEY INSIGHTS:
                - insight 1
                - insight 2
                - insight 3

                PATTERNS AND TRENDS:
                <patterns>

                RISKS OR ANOMALIES:
                <risks>

                BUSINESS RECOMMENDATION:
                <recommendation>

                DATA LIMITATIONS:
                <limitations>

                Analysis Task:
                """ + task;

        Map<String, Object> request = Map.of(
                "model", "llama3.2:3b",
                "prompt", prompt,
                "stream", false
        );

        Map<?, ?> response = restTemplate.postForObject(
                "http://localhost:11434/api/generate",
                request,
                Map.class
        );

        if (response == null || response.get("response") == null) {
            return "Data Analyst Agent did not generate a response.";
        }

        return response.get("response").toString();
    }
}