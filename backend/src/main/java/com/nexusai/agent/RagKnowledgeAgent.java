package com.nexusai.agent;

import com.nexusai.entity.DocumentChunk;
import com.nexusai.repository.DocumentChunkRepository;
import com.nexusai.service.AnalyticsService;
import com.nexusai.service.LLMService;
import com.nexusai.service.OllamaEmbeddingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class RagKnowledgeAgent {

    private final DocumentChunkRepository documentChunkRepository;
    private final OllamaEmbeddingService embeddingService;
    private final AnalyticsService analyticsService;
    private final LLMService llmService;

    public RagKnowledgeAgent(
            DocumentChunkRepository documentChunkRepository,
            OllamaEmbeddingService embeddingService,
            AnalyticsService analyticsService,
            LLMService llmService
    ) {
        this.documentChunkRepository = documentChunkRepository;
        this.embeddingService = embeddingService;
        this.analyticsService = analyticsService;
        this.llmService = llmService;
    }

    public String ask(String question) {

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException(
                    "Knowledge question cannot be empty"
            );
        }

        LocalDateTime startedAt =
                LocalDateTime.now();

        long startTime =
                System.currentTimeMillis();

        try {

            /*
             * 1. Generate embedding for the user question.
             */

            List<Double> questionEmbedding =
                    embeddingService.generateEmbedding(question);

            /*
             * 2. Load document chunks.
             */

            List<DocumentChunk> chunks =
                    documentChunkRepository.findAll();

            if (chunks.isEmpty()) {

                return "No enterprise knowledge has been uploaded yet.";
            }

            /*
             * 3. Find the most similar chunks.
             */

            List<DocumentChunk> relevantChunks =
                    chunks.stream()
                            .filter(chunk ->
                                    chunk.getEmbedding() != null
                                            && !chunk.getEmbedding().isBlank()
                            )
                            .sorted(
                                    Comparator.comparingDouble(
                                            chunk -> -cosineSimilarity(
                                                    questionEmbedding,
                                                    parseEmbedding(
                                                            chunk.getEmbedding()
                                                    )
                                            )
                                    )
                            )
                            .limit(5)
                            .toList();

            /*
             * 4. Build knowledge context.
             */

            StringBuilder context =
                    new StringBuilder();

            for (DocumentChunk chunk : relevantChunks) {

                context.append(
                        "\n--- DOCUMENT CHUNK ---\n"
                );

                context.append(
                        chunk.getContent()
                );
            }

            /*
             * 5. Prepare RAG system instructions.
             */

            String systemPrompt = """
                    You are the NEXUS AI RAG Knowledge Agent.

                    Your job is to answer questions using the
                    enterprise knowledge retrieved from documents.

                    IMPORTANT RULES:

                    1. Use the provided knowledge context.
                    2. Do not invent facts.
                    3. If the answer is not present in the
                       retrieved knowledge, clearly say that
                       the information is not available.
                    4. Give a concise and useful answer.
                    """;

            /*
             * 6. Prepare user prompt with retrieved context.
             */

            String userPrompt = """
                    ENTERPRISE KNOWLEDGE:

                    %s

                    USER QUESTION:

                    %s
                    """.formatted(
                            context,
                            question
                    );

            /*
             * 7. Generate answer.
             *
             * LLMService handles:
             *
             * Gemini → Primary
             * Ollama → Fallback
             */

            String result =
                    llmService.generate(
                            systemPrompt,
                            userPrompt
                    );

            /*
             * 8. Record successful execution.
             */

            long durationMs =
                    System.currentTimeMillis()
                            - startTime;

            analyticsService.recordSuccess(
                    "RAG Knowledge Agent",
                    startedAt,
                    durationMs
            );

            return result;

        } catch (Exception e) {

            /*
             * 9. Record failed execution.
             */

            long durationMs =
                    System.currentTimeMillis()
                            - startTime;

            analyticsService.recordFailure(
                    "RAG Knowledge Agent",
                    startedAt,
                    durationMs,
                    e.getMessage()
            );

            throw e;
        }
    }

    private List<Double> parseEmbedding(
            String embedding
    ) {

        return List.of(
                        embedding.split(",")
                )
                .stream()
                .map(Double::parseDouble)
                .toList();
    }

    private double cosineSimilarity(
            List<Double> a,
            List<Double> b
    ) {

        if (a.size() != b.size()) {
            return -1.0;
        }

        double dotProduct = 0.0;
        double magnitudeA = 0.0;
        double magnitudeB = 0.0;

        for (int i = 0; i < a.size(); i++) {

            double valueA = a.get(i);
            double valueB = b.get(i);

            dotProduct += valueA * valueB;

            magnitudeA += valueA * valueA;

            magnitudeB += valueB * valueB;
        }

        if (magnitudeA == 0 || magnitudeB == 0) {
            return 0.0;
        }

        return dotProduct /
                (
                        Math.sqrt(magnitudeA)
                                * Math.sqrt(magnitudeB)
                );
    }
}