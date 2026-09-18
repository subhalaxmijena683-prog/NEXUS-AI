package com.nexusai.repository;

import com.nexusai.entity.DebateSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DebateSessionRepository
        extends JpaRepository<DebateSession, Long> {

    List<DebateSession> findByUserId(Long userId);

}