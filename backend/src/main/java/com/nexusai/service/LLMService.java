package com.nexusai.service;

import org.springframework.stereotype.Service;

@Service
public class LLMService {

    private final GeminiService geminiService;
    private final OllamaService ollamaService;

    public LLMService(
            GeminiService geminiService,
            OllamaService ollamaService
    ) {
        this.geminiService = geminiService;
        this.ollamaService = ollamaService;
    }

    public String generate(
            String systemPrompt,
            String userPrompt
    ) {

        // =====================================================
        // PRIMARY: GEMINI CLOUD
        // =====================================================

        try {

            System.out.println(
                    "[LLM] Trying Gemini..."
            );

            String response =
                    geminiService.chat(
                            systemPrompt,
                            userPrompt
                    );

            if (
                    response != null
                    && !response.isBlank()
            ) {

                System.out.println(
                        "[LLM] Gemini succeeded."
                );

                return response;
            }

        } catch (Exception e) {

            System.out.println(
                    "[LLM] Gemini failed: "
                            + e.getMessage()
            );
        }

        // =====================================================
        // FALLBACK: OLLAMA
        // =====================================================

        try {

            System.out.println(
                    "[LLM] Falling back to Ollama..."
            );

            String response =
                    ollamaService.chat(
                            systemPrompt,
                            userPrompt
                    );

            if (
                    response != null
                    && !response.isBlank()
            ) {

                System.out.println(
                        "[LLM] Ollama fallback succeeded."
                );

                return response;
            }

        } catch (Exception e) {

            System.out.println(
                    "[LLM] Ollama fallback failed: "
                            + e.getMessage()
            );

            throw new RuntimeException(
                    "Both Gemini and Ollama failed.",
                    e
            );
        }

        throw new RuntimeException(
                "No AI response was generated."
        );
    }
}