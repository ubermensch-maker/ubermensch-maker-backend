package com.example.todo.conversation.dto;

import com.example.todo.conversation.Conversation;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
public class ConversationDto {
    private Long id;
    private Long userId;
    private Long goalId;
    private String title;
    private Instant createdAt;
    private Instant updatedAt;

    public static ConversationDto from(Conversation conversation) {
        ConversationDto response = new ConversationDto();
        response.id = conversation.getId();
        response.userId = conversation.getUser().getId();
        response.goalId = conversation.getGoal().getId();
        response.title = conversation.getTitle();
        response.createdAt = conversation.getCreatedAt();
        response.updatedAt = conversation.getUpdatedAt();
        return response;
    }
}
