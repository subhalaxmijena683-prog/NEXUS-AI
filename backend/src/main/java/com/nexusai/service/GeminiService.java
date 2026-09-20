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
public class GeminiService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String model;

    public GeminiService() {

        Dotenv dotenv = Dotenv.configure()
                .directory("../")
                .ignoreIfMissing()
                .load();

        this.apiKey = dotenv.get("GEMINI_API_KEY");

        this.model = dotenv.get(
                "GEMINI_MODEL",
                "gemini-2.5-flash-lite"
        );

        this.restTemplate = new RestTemplate();
    }

    public String chat(
            String systemPrompt,
            String userPrompt
    ) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "GEMINI_API_KEY is not configured."
            );
        }

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(
                "x-goog-api-key",
                apiKey
        );

        Map<String, Object> requestBody = Map.of(

                "systemInstruction",
                Map.of(
                        "parts",
                        List.of(
                                Map.of(
                                        "text",
                                        systemPrompt
                                )
                        )
                ),

                "contents",
                List.of(
                        Map.of(
                                "role",
                                "user",
                                "parts",
                                List.of(
                                        Map.of(
                                                "text",
                                                userPrompt
                                        )
                                )
                        )
                )
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(
                        requestBody,
                        headers
                );

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/"
                        + model
                        + ":generateContent";

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
                    "Gemini returned an empty response."
            );
        }

        List<?> candidates =
                (List<?>) responseBody.get("candidates");

        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalStateException(
                    "Gemini returned no candidates."
            );
        }

        Map<?, ?> firstCandidate =
                (Map<?, ?>) candidates.get(0);

        Map<?, ?> content =
                (Map<?, ?>) firstCandidate.get("content");

        if (content == null) {
            throw new IllegalStateException(
                    "Gemini response content is missing."
            );
        }

        List<?> parts =
                (List<?>) content.get("parts");

        if (parts == null || parts.isEmpty()) {
            throw new IllegalStateException(
                    "Gemini response parts are empty."
            );
        }

        Map<?, ?> firstPart =
                (Map<?, ?>) parts.get(0);

        Object text =
                firstPart.get("text");

        if (text == null) {
            throw new IllegalStateException(
                    "Gemini response text is empty."
            );
        }

        return text.toString();
    }
}