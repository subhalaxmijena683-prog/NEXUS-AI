package com.nexusai.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OllamaService {

    private final RestTemplate restTemplate;

    public OllamaService() {
        this.restTemplate = new RestTemplate();
    }

    public String chat(
            String systemPrompt,
            String userPrompt
    ) {

        String prompt = systemPrompt
                + "\n\nUser Task:\n"
                + userPrompt;

        Map<String, Object> request = Map.of(
                "model",
                "llama3.2:3b",

                "prompt",
                prompt,

                "stream",
                false
        );

        Map<?, ?> response =
                restTemplate.postForObject(
                        "http://localhost:11434/api/generate",
                        request,
                        Map.class
                );

        if (
                response == null
                || response.get("response") == null
        ) {
            throw new IllegalStateException(
                    "Ollama returned no response."
            );
        }

        return response
                .get("response")
                .toString();
    }
}