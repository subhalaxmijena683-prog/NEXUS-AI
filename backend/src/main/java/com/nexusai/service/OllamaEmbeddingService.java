package com.nexusai.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class OllamaEmbeddingService {

    private final RestTemplate restTemplate;

    public OllamaEmbeddingService() {
        this.restTemplate = new RestTemplate();
    }

    public List<Double> generateEmbedding(String text) {

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "Text cannot be empty"
            );
        }

        String url = "http://localhost:11434/api/embed";

        Map<String, Object> request = Map.of(
                "model", "nomic-embed-text",
                "input", text
        );

        Map<?, ?> response = restTemplate.postForObject(
                url,
                request,
                Map.class
        );

        if (response == null || response.get("embeddings") == null) {
            throw new RuntimeException(
                    "Ollama did not return an embedding"
            );
        }

        List<?> embeddings =
                (List<?>) response.get("embeddings");

        if (embeddings.isEmpty()) {
            throw new RuntimeException(
                    "Empty embedding returned by Ollama"
            );
        }

        List<?> firstEmbedding =
                (List<?>) embeddings.get(0);

        return firstEmbedding.stream()
                .map(value ->
                        ((Number) value).doubleValue()
                )
                .toList();
    }
}