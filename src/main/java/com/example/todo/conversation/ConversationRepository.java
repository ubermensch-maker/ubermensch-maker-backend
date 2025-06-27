package com.example.todo.conversation;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
  List<Conversation> findAllByUserId(Long userId);

  List<Conversation> findAllByUserIdAndGoalId(Long userId, Long goalId);
}
