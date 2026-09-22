package com.nexusai.agent;

import com.nexusai.service.AnalyticsService;
import com.nexusai.service.LLMService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SolutionAgent {

    private final LLMService llmService;
    private final AnalyticsService analyticsService;

    public SolutionAgent(
            LLMService llmService,
            AnalyticsService analyticsService
    ) {
        this.llmService = llmService;
        this.analyticsService = analyticsService;
    }

    public String designSolution(String problem) {

        if (problem == null || problem.isBlank()) {
            throw new IllegalArgumentException(
                    "Solution problem cannot be empty"
            );
        }

        LocalDateTime startedAt = LocalDateTime.now();
        long startTime = System.currentTimeMillis();

        String systemPrompt = """
                You are the NEXUS AI Solution Agent.

                Your responsibility is to transform an enterprise
                problem, prediction and decision into a practical
                implementable solution.

                IMPORTANT:
                - Do not invent company-specific facts.
                - Make the architecture practical.
                - Include implementation steps.
                - Identify risks and mitigations.
                - Define measurable success metrics.

                Provide the response using these sections:

                PROPOSED SOLUTION

                ARCHITECTURE

                IMPLEMENTATION PLAN

                RISKS AND MITIGATIONS

                SUCCESS METRICS
                """;

        try {

            String result = llmService.generate(
                    systemPrompt,
                    problem
            );

            analyticsService.recordSuccess(
                    "Solution Agent",
                    startedAt,
                    System.currentTimeMillis() - startTime
            );

            return result;

        } catch (Exception e) {

            analyticsService.recordFailure(
                    "Solution Agent",
                    startedAt,
                    System.currentTimeMillis() - startTime,
                    e.getMessage()
            );

            throw e;
        }
    }
}
