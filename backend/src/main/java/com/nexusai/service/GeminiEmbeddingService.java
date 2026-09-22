package com.nexusai.service;

import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiEmbeddingService {

    private final RestTemplate restTemplate;
    private final String apiKey;

    private static final String MODEL =
            "gemini-embedding-001";

    public GeminiEmbeddingService() {

        Dotenv dotenv = Dotenv.configure()
                .directory("../")
                .ignoreIfMissing()
                .load();

        this.apiKey =
                dotenv.get("GEMINI_API_KEY");

        this.restTemplate =
                new RestTemplate();
    }

    public List<Double> generateEmbedding(
            String text
    ) {
        return generateEmbedding(
                text,
                "RETRIEVAL_QUERY"
        );
    }

    public List<Double> generateDocumentEmbedding(
            String text
    ) {
        return generateEmbedding(
                text,
                "RETRIEVAL_DOCUMENT"
        );
    }

    private List<Double> generateEmbedding(
            String text,
            String taskType
    ) {

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "Text cannot be empty"
            );
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "GEMINI_API_KEY is not configured."
            );
        }

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        headers.set(
                "x-goog-api-key",
                apiKey
        );

        Map<String, Object> requestBody =
                Map.of(
                        "content",
                        Map.of(
                                "parts",
                                List.of(
                                        Map.of(
                                                "text",
                                                text
                                        )
                                )
                        ),
                        "taskType",
                        taskType
                );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(
                        requestBody,
                        headers
                );

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/"
                        + MODEL
                        + ":embedContent";

        ResponseEntity<Map> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request,
                        Map.class
                );

        Map<?, ?> responseBody =
                response.getBody();

        if (responseBody == null) {
            throw new IllegalStateException(
                    "Gemini embedding response is empty."
            );
        }

        Object embeddingObject =
                responseBody.get("embedding");

        if (!(embeddingObject instanceof Map)) {
            throw new IllegalStateException(
                    "Gemini did not return an embedding."
            );
        }

        Map<?, ?> embedding =
                (Map<?, ?>) embeddingObject;

        Object valuesObject =
                embedding.get("values");

        if (!(valuesObject instanceof List)) {
            throw new IllegalStateException(
                    "Gemini embedding values are missing."
            );
        }

        List<?> values =
                (List<?>) valuesObject;

        return values.stream()
                .map(value ->
                        ((Number) value).doubleValue()
                )
                .toList();
    }
}