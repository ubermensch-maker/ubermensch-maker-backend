package com.example.todo.message;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
  List<Message> findAllByConversationId(Long conversationId, Sort sort);

  void deleteAllByConversationId(Long conversationId);
}
