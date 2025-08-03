package com.example.todo.conversation;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
  List<Conversation> findAllByUserId(Long userId);
}
