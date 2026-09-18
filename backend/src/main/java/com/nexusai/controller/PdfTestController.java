package com.nexusai.controller;

import com.nexusai.service.PdfTextExtractorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pdf")
public class PdfTestController {

    private final PdfTextExtractorService pdfTextExtractorService;

    public PdfTestController(PdfTextExtractorService pdfTextExtractorService) {
        this.pdfTextExtractorService = pdfTextExtractorService;
    }

    @PostMapping("/extract")
    public ResponseEntity<String> extractText(
            @RequestParam("file") MultipartFile file
    ) {

        String text = pdfTextExtractorService.extractText(file);

        return ResponseEntity.ok(text);
    }
}