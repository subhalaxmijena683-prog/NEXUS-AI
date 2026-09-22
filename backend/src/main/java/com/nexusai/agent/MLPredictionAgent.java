package com.nexusai.agent;

import com.nexusai.service.AnalyticsService;
import com.nexusai.service.LLMService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MLPredictionAgent {

    private final LLMService llmService;
    private final AnalyticsService analyticsService;

    public MLPredictionAgent(
            LLMService llmService,
            AnalyticsService analyticsService
    ) {
        this.llmService = llmService;
        this.analyticsService = analyticsService;
    }

    public String predict(String task) {

        if (task == null || task.isBlank()) {
            throw new IllegalArgumentException(
                    "Prediction task cannot be empty"
            );
        }

        LocalDateTime startedAt = LocalDateTime.now();
        long startTime = System.currentTimeMillis();

        String systemPrompt = """
                You are the NEXUS AI ML Prediction Agent.

                Your responsibility is to analyze an enterprise
                problem and generate a scenario-based prediction.

                IMPORTANT:
                - Do not invent historical data.
                - Do not invent numerical results.
                - Clearly identify assumptions.
                - Use the available research and analysis.
                - Explain uncertainty and limitations.

                Provide the response using these sections:

                PREDICTION

                KEY DRIVERS

                ASSUMPTIONS

                CONFIDENCE AND LIMITATIONS

                BUSINESS IMPLICATION
                """;

        try {

            String result = llmService.generate(
                    systemPrompt,
                    task
            );

            analyticsService.recordSuccess(
                    "ML Prediction Agent",
                    startedAt,
                    System.currentTimeMillis() - startTime
            );

            return result;

        } catch (Exception e) {

            analyticsService.recordFailure(
                    "ML Prediction Agent",
                    startedAt,
                    System.currentTimeMillis() - startTime,
                    e.getMessage()
            );

            throw e;
        }
    }
}
