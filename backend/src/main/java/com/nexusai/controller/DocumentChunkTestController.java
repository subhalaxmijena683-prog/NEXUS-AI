package com.nexusai.controller;

import com.nexusai.entity.Document;
import com.nexusai.repository.DocumentRepository;
import com.nexusai.service.DocumentChunkerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents/test")
public class DocumentChunkTestController {

    private final DocumentRepository documentRepository;
    private final DocumentChunkerService documentChunkerService;

    public DocumentChunkTestController(
            DocumentRepository documentRepository,
            DocumentChunkerService documentChunkerService
    ) {
        this.documentRepository = documentRepository;
        this.documentChunkerService = documentChunkerService;
    }

    @GetMapping("/{id}/chunks")
    public ResponseEntity<List<String>> getDocumentChunks(
            @PathVariable Long id
    ) {

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        List<String> chunks =
                documentChunkerService.chunkText(
                        document.getExtractedText()
                );

        return ResponseEntity.ok(chunks);
    }
}