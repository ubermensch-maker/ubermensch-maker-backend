package com.example.todo.message.dto;

import com.example.todo.message.Message;
import com.example.todo.message.enums.MessageRole;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MessageDto {
  private Long id;
  private Long userId;
  private Long conversationId;
  private String model;
  private MessageRole role;
  private List<ContentDto> content;
  private Instant createdAt;
  private Instant updatedAt;

  public static MessageDto from(Message message) {
    MessageDto response = new MessageDto();
    response.id = message.getId();
    response.userId = message.getUser() != null ? message.getUser().getId() : null;
    response.conversationId = message.getConversation().getId();
    response.model = message.getModel();
    response.role = message.getRole();
    response.content = message.getContent();
    response.createdAt = message.getCreatedAt();
    response.updatedAt = message.getUpdatedAt();
    return response;
  }
}
