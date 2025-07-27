package com.example.todo.message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {
  List<Message> findAllByConversationId(UUID conversationId, Sort sort);

  void deleteAllByConversationId(UUID conversationId);
  
  Optional<Message> findTopByConversationIdOrderByIndexDesc(UUID conversationId);
}
