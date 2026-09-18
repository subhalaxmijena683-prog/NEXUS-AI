package com.nexusai.agent;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ResearchAgent {

    private final RestTemplate restTemplate;

    public ResearchAgent() {
        this.restTemplate = new RestTemplate();
    }

    public String research(String task) {

        if (task == null || task.isBlank()) {
            throw new IllegalArgumentException("Research task cannot be empty");
        }

        String prompt = """
                You are the NEXUS AI Research Agent.

                Your responsibility is to research and analyze a
                business or enterprise problem.

                Follow these steps:

                1. Understand the research question.
                2. Identify important facts or concepts.
                3. Analyze the available information.
                4. Identify important findings.
                5. Provide a clear research summary.
                6. Mention limitations when information is insufficient.

                Do not pretend to access the internet or private
                enterprise data unless it is actually provided.

                Give the result in this format:

                RESEARCH OBJECTIVE:
                <objective>

                KEY FINDINGS:
                - finding 1
                - finding 2
                - finding 3

                ANALYSIS:
                <analysis>

                RECOMMENDATION:
                <recommendation>

                LIMITATIONS:
                <limitations>

                Research Task:
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
            return "Research Agent did not generate a response.";
        }

        return response.get("response").toString();
    }
}