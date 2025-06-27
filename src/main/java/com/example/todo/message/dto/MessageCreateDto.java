package com.example.todo.message.dto;

import com.example.todo.message.enums.MessageRole;
import java.util.List;
import lombok.Getter;

@Getter
public class MessageCreateDto {
  private Long conversationId;
  private String model;
  private MessageRole role;
  private List<ContentDto> content;
}
