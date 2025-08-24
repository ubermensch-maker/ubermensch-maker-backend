package com.example.todo.admin;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemPromptRepository extends JpaRepository<SystemPrompt, Long> {
  Optional<SystemPrompt> findByName(String name);
}