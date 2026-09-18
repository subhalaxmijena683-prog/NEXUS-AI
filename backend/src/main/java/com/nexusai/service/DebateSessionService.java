package com.nexusai.service;

import com.nexusai.dto.CreateDebateSessionRequest;
import com.nexusai.entity.DebateSession;
import com.nexusai.entity.User;
import com.nexusai.repository.DebateSessionRepository;
import com.nexusai.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DebateSessionService {

    private final DebateSessionRepository debateSessionRepository;
    private final UserRepository userRepository;

    public DebateSessionService(
            DebateSessionRepository debateSessionRepository,
            UserRepository userRepository
    ) {
        this.debateSessionRepository = debateSessionRepository;
        this.userRepository = userRepository;
    }

    public DebateSession createSession(
            CreateDebateSessionRequest request,
            String email
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DebateSession session = new DebateSession();

        session.setTopic(request.getTopic());
        session.setPosition(request.getPosition());
        session.setCreatedAt(LocalDateTime.now());
        session.setUser(user);

        return debateSessionRepository.save(session);
    }

    public List<DebateSession> getUserSessions(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return debateSessionRepository.findByUserId(user.getId());
    }
}