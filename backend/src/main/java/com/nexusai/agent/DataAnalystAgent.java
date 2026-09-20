package com.nexusai.agent;

import com.nexusai.service.AnalyticsService;
import com.nexusai.service.LLMService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DataAnalystAgent {

    private final LLMService llmService;
    private final AnalyticsService analyticsService;

    public DataAnalystAgent(
            LLMService llmService,
            AnalyticsService analyticsService
    ) {
        this.llmService = llmService;
        this.analyticsService = analyticsService;
    }

    public String analyze(String task) {

        if (task == null || task.isBlank()) {
            throw new IllegalArgumentException(
                    "Data analysis task cannot be empty"
            );
        }

        LocalDateTime startedAt =
                LocalDateTime.now();

        long startTime =
                System.currentTimeMillis();

        String systemPrompt = """
                You are the NEXUS AI Data Analyst Agent.

                Your responsibility is to analyze business,
                operational, financial, or enterprise data
                and convert it into useful insights.

                Follow these steps:

                1. Understand the analysis request.
                2. Identify important metrics and variables.
                3. Analyze the provided information.
                4. Identify patterns and trends.
                5. Identify risks or anomalies.
                6. Provide actionable business insights.
                7. Clearly mention when actual data is missing.

                IMPORTANT:
                Do not invent real data.

                If numerical data has not been provided,
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
                    "Data Analyst Agent",
                    startedAt,
                    durationMs
            );

            return result;

        } catch (Exception e) {

            long durationMs =
                    System.currentTimeMillis()
                            - startTime;

            analyticsService.recordFailure(
                    "Data Analyst Agent",
                    startedAt,
                    durationMs,
                    e.getMessage()
            );

            throw e;
        }
    }
}