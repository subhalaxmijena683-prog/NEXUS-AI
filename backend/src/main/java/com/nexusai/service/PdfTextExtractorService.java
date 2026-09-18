package com.nexusai.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PdfTextExtractorService {

    public String extractText(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("PDF file cannot be empty");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || !fileName.toLowerCase().endsWith(".pdf")) {
            throw new RuntimeException("Only PDF files are supported");
        }

        try {
            byte[] fileBytes = file.getBytes();

            try (PDDocument document = Loader.loadPDF(fileBytes)) {

                PDFTextStripper stripper = new PDFTextStripper();

                String text = stripper.getText(document);

                if (text == null || text.isBlank()) {
                    throw new RuntimeException(
                            "No readable text found in the PDF"
                    );
                }

                return text.trim();
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to extract text from PDF",
                    e
            );
        }
    }
}