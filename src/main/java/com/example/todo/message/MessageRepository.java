package com.example.todo.message;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByUserIdAndConversationId(Long userId, Long conversationId);

    void deleteAllByConversationId(Long conversationId);
}
