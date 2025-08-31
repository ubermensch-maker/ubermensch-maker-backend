package com.example.todo.admin;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromptTemplateRepository extends JpaRepository<PromptTemplate, Long> {
  Optional<PromptTemplate> findByName(String name);
}
