package com.nexusai.agent;

import com.nexusai.entity.DocumentChunk;
import com.nexusai.repository.DocumentChunkRepository;
import com.nexusai.service.AnalyticsService;
import com.nexusai.service.GeminiEmbeddingService;
import com.nexusai.service.LLMService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class RagKnowledgeAgent {

    private final DocumentChunkRepository documentChunkRepository;
    private final GeminiEmbeddingService embeddingService;
    private final AnalyticsService analyticsService;
    private final LLMService llmService;

    public RagKnowledgeAgent(
            DocumentChunkRepository documentChunkRepository,
            GeminiEmbeddingService embeddingService,
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

        LocalDateTime startedAt = LocalDateTime.now();
        long startTime = System.currentTimeMillis();

        try {

            System.out.println("[RAG] Question: " + question);

            // 1. Generate Gemini embedding for the user question
            List<Double> questionEmbedding =
                    embeddingService.generateEmbedding(question);

            System.out.println(
                    "[RAG] Question embedding size: "
                            + questionEmbedding.size()
            );

            // 2. Load document chunks
            List<DocumentChunk> chunks =
                    documentChunkRepository.findAll();

            System.out.println(
                    "[RAG] Total document chunks: "
                            + chunks.size()
            );

            if (chunks.isEmpty()) {

                System.out.println(
                        "[RAG] No document chunks found."
                );

                return "No enterprise knowledge has been uploaded yet.";
            }

            // 3. Keep only chunks with embeddings
            List<DocumentChunk> embeddedChunks =
                    chunks.stream()
                            .filter(chunk ->
                                    chunk.getEmbedding() != null
                                            && !chunk.getEmbedding().isBlank()
                            )
                            .toList();

            System.out.println(
                    "[RAG] Chunks with embeddings: "
                            + embeddedChunks.size()
            );

            if (embeddedChunks.isEmpty()) {

                System.out.println(
                        "[RAG] Document chunks exist, but no embeddings were found."
                );

                return "The document was uploaded, but its knowledge embeddings are not available yet. Please upload the document again.";
            }

            // 4. Find most similar chunks
            List<DocumentChunk> relevantChunks =
                    embeddedChunks.stream()
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

            System.out.println(
                    "[RAG] Relevant chunks selected: "
                            + relevantChunks.size()
            );

            // 5. Build knowledge context
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

            // 6. RAG system prompt
            String systemPrompt = """
                    You are the NEXUS AI RAG Knowledge Agent.

                    Your job is to answer questions using the
                    enterprise knowledge retrieved from documents.

                    IMPORTANT RULES:

                    1. Use only the provided knowledge context.
                    2. Do not invent facts.
                    3. If the answer is not present in the context,
                       clearly say that the information is not available.
                    4. Give a concise and useful answer.
                    """;

            // 7. User prompt
            String userPrompt = """
                    ENTERPRISE KNOWLEDGE:

                    %s

                    USER QUESTION:

                    %s
                    """.formatted(
                            context,
                            question
                    );

            // 8. Generate answer using Gemini
            String result =
                    llmService.generate(
                            systemPrompt,
                            userPrompt
                    );

            // 9. Record success
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

            long durationMs =
                    System.currentTimeMillis()
                            - startTime;

            analyticsService.recordFailure(
                    "RAG Knowledge Agent",
                    startedAt,
                    durationMs,
                    e.getMessage()
            );

            System.out.println(
                    "[RAG] ERROR: "
                            + e.getMessage()
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

            System.out.println(
                    "[RAG] Embedding dimension mismatch: "
                            + a.size()
                            + " vs "
                            + b.size()
            );

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