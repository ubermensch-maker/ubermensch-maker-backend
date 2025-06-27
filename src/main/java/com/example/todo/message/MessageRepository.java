package com.example.todo.message;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
  List<Message> findAllByUserIdAndConversationId(Long userId, Long conversationId);

  void deleteAllByConversationId(Long conversationId);
}
