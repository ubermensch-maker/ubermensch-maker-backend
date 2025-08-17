package com.example.todo.message.dto;

import com.example.todo.message.enums.MessageRole;
import com.example.todo.message.enums.Model;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class MessageCreateDto {
  private UUID conversationId;
  private UUID parentMessageId;
  private Model model;
  private MessageRole role;
  private List<ContentDto> content;
}
