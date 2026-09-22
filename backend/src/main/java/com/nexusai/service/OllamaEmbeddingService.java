package com.nexusai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;

@Service
public class OllamaEmbeddingService {

    private final RestTemplate restTemplate;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    public OllamaEmbeddingService() {
        this.restTemplate = new RestTemplate();
    }

    public List<Double> generateEmbedding(String text) {

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be empty");
        }

        String url = "https://api.openai.com/v1/embeddings";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        Map<String, Object> request = Map.of(
                "model", "text-embedding-3-small",
                "input", text
        );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(request, headers);

        Map<?, ?> response = restTemplate.postForObject(
                url,
                entity,
                Map.class
        );

        if (response == null || response.get("data") == null) {
            throw new RuntimeException(
                    "OpenAI did not return an embedding"
            );
        }

        List<?> data = (List<?>) response.get("data");

        if (data.isEmpty()) {
            throw new RuntimeException(
                    "Empty embedding returned by OpenAI"
            );
        }

        Map<?, ?> firstData = (Map<?, ?>) data.get(0);

        List<?> embedding =
                (List<?>) firstData.get("embedding");

        if (embedding == null || embedding.isEmpty()) {
            throw new RuntimeException(
                    "Embedding vector is empty"
            );
        }

        return embedding.stream()
                .map(value -> ((Number) value).doubleValue())
                .toList();
    }
}