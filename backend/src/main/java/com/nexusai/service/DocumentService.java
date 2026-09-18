package com.nexusai.service;

import com.nexusai.entity.Document;
import com.nexusai.entity.User;
import com.nexusai.repository.DocumentRepository;
import com.nexusai.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final PdfTextExtractorService pdfTextExtractorService;
    private final DocumentChunkService documentChunkService;

    private final Path uploadDirectory =
            Paths.get("uploads");

    public DocumentService(
            DocumentRepository documentRepository,
            UserRepository userRepository,
            PdfTextExtractorService pdfTextExtractorService,
            DocumentChunkService documentChunkService
    ) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.pdfTextExtractorService = pdfTextExtractorService;
        this.documentChunkService = documentChunkService;

        try {
            Files.createDirectories(uploadDirectory);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not create upload directory",
                    e
            );
        }
    }

    public Document createDocument(
            MultipartFile file,
            String email
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (file.isEmpty()) {
            throw new RuntimeException("File cannot be empty");
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new RuntimeException("Invalid file name");
        }

        String storedFileName =
                UUID.randomUUID() + "_" + originalFileName;

        Path filePath = uploadDirectory.resolve(storedFileName);

        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to save file",
                    e
            );
        }

        String extractedText =
                pdfTextExtractorService.extractText(file);

        Document document = Document.builder()
                .fileName(originalFileName)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .filePath(filePath.toString())
                .extractedText(extractedText)
                .uploadedAt(LocalDateTime.now())
                .user(user)
                .build();

        Document savedDocument =
                documentRepository.save(document);

        documentChunkService.chunkAndSave(
                savedDocument,
                extractedText
        );

        return savedDocument;
    }

    public List<Document> getUserDocuments(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return documentRepository.findByUserId(user.getId());
    }
}