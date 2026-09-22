package com.nexusai.service;

import com.nexusai.entity.Document;
import com.nexusai.entity.DocumentChunk;
import com.nexusai.repository.DocumentChunkRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentChunkService {

    private final DocumentChunkRepository documentChunkRepository;
    private final GeminiEmbeddingService embeddingService;

    public DocumentChunkService(
            DocumentChunkRepository documentChunkRepository,
            GeminiEmbeddingService embeddingService
    ) {
        this.documentChunkRepository = documentChunkRepository;
        this.embeddingService = embeddingService;
    }

    public List<DocumentChunk> chunkAndSave(
            Document document,
            String text
    ) {

        int chunkSize = 500;
        int overlap = 50;

        List<DocumentChunk> chunks =
                new ArrayList<>();

        if (text == null || text.isBlank()) {
            return chunks;
        }

        int start = 0;
        int chunkIndex = 0;

        while (start < text.length()) {

            int end = Math.min(
                    start + chunkSize,
                    text.length()
            );

            String chunkText =
                    text.substring(start, end);

            // Generate Gemini embedding
            List<Double> embedding =
        embeddingService.generateDocumentEmbedding(chunkText);

            String embeddingString =
                    embedding.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));

            DocumentChunk chunk =
                    DocumentChunk.builder()
                            .document(document)
                            .chunkIndex(chunkIndex)
                            .content(chunkText)
                            .embedding(embeddingString)
                            .build();

            chunks.add(chunk);

            chunkIndex++;

            if (end == text.length()) {
                break;
            }

            start = end - overlap;
        }

        return documentChunkRepository.saveAll(chunks);
    }
}