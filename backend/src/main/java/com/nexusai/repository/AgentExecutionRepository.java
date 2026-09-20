package com.nexusai.repository;

import com.nexusai.entity.AgentExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentExecutionRepository extends JpaRepository<AgentExecution, Long> {
}