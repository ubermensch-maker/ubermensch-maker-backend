package com.example.todo.conversation.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConversationListDto {
  private int total;
  private List<ConversationDto> items;
}
