package com.nexusai.controller;

import com.nexusai.dto.DocumentResponse;
import com.nexusai.entity.Document;
import com.nexusai.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {

        Document document = documentService.createDocument(
                file,
                authentication.getName()
        );

        DocumentResponse response = new DocumentResponse(
                document.getId(),
                document.getFileName(),
                document.getFileType(),
                document.getFileSize(),
                document.getUploadedAt()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getMyDocuments(
            Authentication authentication
    ) {

        List<Document> documents =
                documentService.getUserDocuments(
                        authentication.getName()
                );

        List<DocumentResponse> response = documents.stream()
                .map(document -> new DocumentResponse(
                        document.getId(),
                        document.getFileName(),
                        document.getFileType(),
                        document.getFileSize(),
                        document.getUploadedAt()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}