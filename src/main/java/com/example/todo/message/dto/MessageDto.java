package com.example.todo.message.dto;

import com.example.todo.conversation.dto.ConversationDto;
import com.example.todo.message.Message;
import com.example.todo.message.enums.MessageRole;
import com.example.todo.message.enums.Model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MessageDto {
  private UUID id;
  private Long userId;
  private UUID conversationId;
  private UUID parentMessageId;
  private Integer index;
  private Model model;
  private MessageRole role;
  private List<ContentDto> content;
  private Instant createdAt;
  private Instant updatedAt;
  private ConversationDto conversation;

  public static MessageDto from(Message message) {
    MessageDto response = new MessageDto();
    response.id = message.getId();
    response.userId = message.getUser() != null ? message.getUser().getId() : null;
    response.conversationId = message.getConversation().getId();
    response.parentMessageId =
        message.getParentMessage() != null ? message.getParentMessage().getId() : null;
    response.index = message.getIndex();
    response.model = message.getModel();
    response.role = message.getRole();
    response.content = message.getContent();
    response.createdAt = message.getCreatedAt();
    response.updatedAt = message.getUpdatedAt();
    return response;
  }
}
