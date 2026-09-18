package com.nexusai.repository;

import com.nexusai.entity.Debate;
import com.nexusai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DebateRepository extends JpaRepository<Debate, Long> {

    List<Debate> findByUser(User user);
}