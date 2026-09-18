package com.nexusai.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentChunkerService {

    private static final int CHUNK_SIZE = 1000;
    private static final int CHUNK_OVERLAP = 200;

    public List<String> chunkText(String text) {

        if (text == null || text.isBlank()) {
            throw new RuntimeException("Text cannot be empty");
        }

        // Clean the text
        String cleanedText = text
                .replace("\r", " ")
                .replace("\n", " ")
                .replaceAll("\\s+", " ")
                .trim();

        List<String> chunks = new ArrayList<>();

        int start = 0;

        while (start < cleanedText.length()) {

            int end = Math.min(
                    start + CHUNK_SIZE,
                    cleanedText.length()
            );

            String chunk = cleanedText.substring(start, end).trim();

            if (!chunk.isBlank()) {
                chunks.add(chunk);
            }

            if (end == cleanedText.length()) {
                break;
            }

            start = end - CHUNK_OVERLAP;
        }

        return chunks;
    }
}