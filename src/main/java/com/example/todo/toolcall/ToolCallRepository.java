package com.example.todo.toolcall;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToolCallRepository extends JpaRepository<ToolCall, UUID> {
  List<ToolCall> findByMessageId(UUID messageId);
}