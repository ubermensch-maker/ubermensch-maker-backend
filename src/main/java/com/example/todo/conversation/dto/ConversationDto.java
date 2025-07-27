package com.example.todo.conversation.dto;

import com.example.todo.conversation.Conversation;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ConversationDto {
  private UUID id;
  private Long userId;
  private String title;
  private Instant createdAt;
  private Instant updatedAt;

  public static ConversationDto from(Conversation conversation) {
    ConversationDto response = new ConversationDto();
    response.id = conversation.getId();
    response.userId = conversation.getUser().getId();
    response.title = conversation.getTitle();
    response.createdAt = conversation.getCreatedAt();
    response.updatedAt = conversation.getUpdatedAt();
    return response;
  }
}
