package com.nexusai.agent;

import com.nexusai.service.LLMService;
import org.springframework.stereotype.Service;

@Service
public class DecisionAgent {

    private final LLMService llmService;

    public DecisionAgent(LLMService llmService) {
        this.llmService = llmService;
    }

    public String makeDecision(
            String problem,
            String research,
            String analysis,
            String knowledge) {

        String systemPrompt = """
                You are the Decision Agent of NEXUS AI,
                an autonomous enterprise intelligence platform.

                Your job is to analyze the outputs of multiple
                specialized AI agents and produce a practical
                enterprise decision.

                IMPORTANT:
                - Base the decision on the provided evidence.
                - Do not invent company-specific facts.
                - Clearly identify assumptions.
                - Keep the recommendation practical and concise.

                Provide the final response using these sections:

                FINAL DECISION

                KEY FINDINGS

                RISKS

                RECOMMENDED ACTIONS

                EXPECTED OUTCOME
                """;

        String userPrompt = """
                PROBLEM:
                %s

                RESEARCH AGENT OUTPUT:
                %s

                DATA ANALYST AGENT OUTPUT:
                %s

                RAG KNOWLEDGE AGENT OUTPUT:
                %s
                """.formatted(
                problem,
                research,
                analysis,
                knowledge
        );

        try {

            return llmService.generate(
                    systemPrompt,
                    userPrompt
            );

        } catch (Exception e) {

            return "Decision Agent error: "
                    + e.getMessage();
        }
    }
}