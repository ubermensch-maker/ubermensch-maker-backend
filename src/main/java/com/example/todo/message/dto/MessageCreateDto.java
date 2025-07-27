package com.example.todo.message.dto;

import com.example.todo.message.enums.MessageRole;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class MessageCreateDto {
  private UUID conversationId;
  private UUID parentMessageId;
  private String model;
  private MessageRole role;
  private List<ContentDto> content;
}
