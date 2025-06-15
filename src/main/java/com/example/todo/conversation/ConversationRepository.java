package com.example.todo.conversation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findAllByUserId(Long userId);

    List<Conversation> findAllByUserIdAndGoalId(Long userId, Long goalId);
}
