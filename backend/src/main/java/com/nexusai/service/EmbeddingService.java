package com.nexusai.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmbeddingService {

    public EmbeddingService() {
        // Local embedding implementation will be added later.
        // OpenAI credentials are intentionally not required.
    }

    public List<Float> generateEmbedding(String text) {

        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        // Temporary placeholder.
        // Real local embeddings will be connected when RAG is implemented.
        return new ArrayList<>();
    }
}