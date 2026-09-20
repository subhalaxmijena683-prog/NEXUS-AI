package com.nexusai.agent;

import com.nexusai.service.AnalyticsService;
import com.nexusai.service.LLMService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ResearchAgent {

    private final LLMService llmService;
    private final AnalyticsService analyticsService;

    public ResearchAgent(
            LLMService llmService,
            AnalyticsService analyticsService
    ) {
        this.llmService = llmService;
        this.analyticsService = analyticsService;
    }

    public String research(String task) {

        if (task == null || task.isBlank()) {
            throw new IllegalArgumentException(
                    "Research task cannot be empty"
            );
        }

        LocalDateTime startedAt =
                LocalDateTime.now();

        long startTime =
                System.currentTimeMillis();

        String systemPrompt = """
                You are the NEXUS AI Research Agent.

                Your responsibility is to research and analyze
                business and enterprise problems.

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
                """;

        try {

            String result =
                    llmService.generate(
                            systemPrompt,
                            task
                    );

            long durationMs =
                    System.currentTimeMillis()
                            - startTime;

            analyticsService.recordSuccess(
                    "Research Agent",
                    startedAt,
                    durationMs
            );

            return result;

        } catch (Exception e) {

            long durationMs =
                    System.currentTimeMillis()
                            - startTime;

            analyticsService.recordFailure(
                    "Research Agent",
                    startedAt,
                    durationMs,
                    e.getMessage()
            );

            throw e;
        }
    }
}