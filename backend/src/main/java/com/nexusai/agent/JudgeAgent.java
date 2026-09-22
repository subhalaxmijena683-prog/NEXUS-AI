package com.nexusai.agent;

import com.nexusai.service.AnalyticsService;
import com.nexusai.service.LLMService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class JudgeAgent {

    private final LLMService llmService;
    private final AnalyticsService analyticsService;

    public JudgeAgent(
            LLMService llmService,
            AnalyticsService analyticsService
    ) {
        this.llmService = llmService;
        this.analyticsService = analyticsService;
    }

    public String evaluate(
            String problem,
            String decision,
            String solution,
            String prediction
    ) {

        LocalDateTime startedAt = LocalDateTime.now();
        long startTime = System.currentTimeMillis();

        String systemPrompt = """
                You are the NEXUS AI Judge Agent.

                You are the final quality-control layer of the
                autonomous enterprise intelligence workflow.

                Evaluate whether the prediction, decision and
                proposed solution properly address the original
                enterprise problem.

                IMPORTANT:
                - Do not invent facts.
                - Identify unsupported assumptions.
                - Identify risks and gaps.
                - Suggest required improvements.
                - Validate whether the final solution is practical.

                Provide the response using these sections:

                EVALUATION

                STRENGTHS

                GAPS

                RISKS

                REQUIRED IMPROVEMENTS

                FINAL VALIDATION
                """;

        String userPrompt = """
                ORIGINAL ENTERPRISE PROBLEM:

                %s


                ML PREDICTION:

                %s


                DECISION:

                %s


                PROPOSED SOLUTION:

                %s
                """.formatted(
                problem,
                prediction,
                decision,
                solution
        );

        try {

            String result = llmService.generate(
                    systemPrompt,
                    userPrompt
            );

            analyticsService.recordSuccess(
                    "Judge Agent",
                    startedAt,
                    System.currentTimeMillis() - startTime
            );

            return result;

        } catch (Exception e) {

            analyticsService.recordFailure(
                    "Judge Agent",
                    startedAt,
                    System.currentTimeMillis() - startTime,
                    e.getMessage()
            );

            throw e;
        }
    }
}
