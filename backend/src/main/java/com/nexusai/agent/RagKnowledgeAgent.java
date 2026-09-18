package com.nexusai.agent;

import com.nexusai.entity.DocumentChunk;
import com.nexusai.repository.DocumentChunkRepository;
import com.nexusai.service.OllamaEmbeddingService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class RagKnowledgeAgent {

    private final DocumentChunkRepository documentChunkRepository;
    private final OllamaEmbeddingService embeddingService;
    private final RestTemplate restTemplate;

    public RagKnowledgeAgent(
            DocumentChunkRepository documentChunkRepository,
            OllamaEmbeddingService embeddingService
    ) {
        this.documentChunkRepository = documentChunkRepository;
        this.embeddingService = embeddingService;
        this.restTemplate = new RestTemplate();
    }

    public String ask(String question) {

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException(
                    "Knowledge question cannot be empty"
            );
        }

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
                                chunk.getEmbedding() != null &&
                                !chunk.getEmbedding().isBlank()
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
         * 5. Ask Ollama using retrieved knowledge.
         */
        String prompt = """
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

                ENTERPRISE KNOWLEDGE:
                """ + context + """

                USER QUESTION:
                """ + question;

        Map<String, Object> request =
                Map.of(
                        "model", "llama3.2:3b",
                        "prompt", prompt,
                        "stream", false
                );

        Map<?, ?> response =
                restTemplate.postForObject(
                        "http://localhost:11434/api/generate",
                        request,
                        Map.class
                );

        if (response == null ||
                response.get("response") == null) {

            return "RAG Knowledge Agent could not generate an answer.";
        }

        return response
                .get("response")
                .toString();
    }

    private List<Double> parseEmbedding(
            String embedding
    ) {

        return List.of(embedding.split(","))
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
                (Math.sqrt(magnitudeA) *
                 Math.sqrt(magnitudeB));
    }
}