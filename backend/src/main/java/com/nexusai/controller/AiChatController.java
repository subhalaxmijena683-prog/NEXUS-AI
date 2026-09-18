package com.nexusai.controller;

import com.nexusai.dto.AiChatRequest;
import com.nexusai.dto.AiChatResponse;
import com.nexusai.service.AiChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(
            @RequestBody AiChatRequest request
    ) {

        String response =
                aiChatService.chat(request.getMessage());

        return ResponseEntity.ok(
                new AiChatResponse(response)
        );
    }
}