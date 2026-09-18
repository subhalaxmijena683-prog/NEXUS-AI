package com.nexusai.controller;

import com.nexusai.dto.CreateDebateSessionRequest;
import com.nexusai.dto.DebateSessionResponse;
import com.nexusai.entity.DebateSession;
import com.nexusai.service.DebateSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/debates")
public class DebateSessionController {

    private final DebateSessionService debateSessionService;

    public DebateSessionController(
            DebateSessionService debateSessionService
    ) {
        this.debateSessionService = debateSessionService;
    }

    @PostMapping
    public ResponseEntity<?> createSession(
            @RequestBody CreateDebateSessionRequest request,
            Authentication authentication
    ) {

        DebateSession session = debateSessionService.createSession(
                request,
                authentication.getName()
        );

        DebateSessionResponse response = new DebateSessionResponse(
                session.getId(),
                session.getTopic(),
                session.getPosition(),
                session.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DebateSessionResponse>> getMySessions(
            Authentication authentication
    ) {

        List<DebateSession> sessions =
                debateSessionService.getUserSessions(
                        authentication.getName()
                );

        List<DebateSessionResponse> response = sessions.stream()
                .map(session -> new DebateSessionResponse(
                        session.getId(),
                        session.getTopic(),
                        session.getPosition(),
                        session.getCreatedAt()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}