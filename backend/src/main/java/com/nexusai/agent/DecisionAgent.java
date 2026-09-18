package com.nexusai.agent;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class DecisionAgent {

    private final RestTemplate restTemplate = new RestTemplate();

    public String makeDecision(
            String problem,
            String research,
            String analysis,
            String knowledge) {

        String prompt = """
                You are the Decision Agent of NEXUS AI,
                an autonomous enterprise intelligence platform.

                Your job is to analyze the outputs of multiple
                specialized AI agents and produce a practical
                enterprise decision.

                PROBLEM:
                %s

                RESEARCH AGENT OUTPUT:
                %s

                DATA ANALYST AGENT OUTPUT:
                %s

                RAG KNOWLEDGE AGENT OUTPUT:
                %s

                Provide the final response using these sections:

                FINAL DECISION

                KEY FINDINGS

                RISKS

                RECOMMENDED ACTIONS

                EXPECTED OUTCOME

                IMPORTANT:
                - Base the decision on the provided evidence.
                - Do not invent company-specific facts.
                - Clearly identify assumptions.
                - Keep the recommendation practical and concise.
                """.formatted(
                problem,
                research,
                analysis,
                knowledge
        );

        try {

            Map<String, Object> request = new HashMap<>();

            request.put("model", "llama3.2:3b");
            request.put("prompt", prompt);
            request.put("stream", false);

            Map<?, ?> response = restTemplate.postForObject(
                    "http://localhost:11434/api/generate",
                    request,
                    Map.class
            );

            if (response != null && response.get("response") != null) {
                return response.get("response").toString();
            }

            return "Decision Agent did not receive a response.";

        } catch (Exception e) {

            return "Decision Agent error: "
                    + e.getMessage();
        }
    }
}
