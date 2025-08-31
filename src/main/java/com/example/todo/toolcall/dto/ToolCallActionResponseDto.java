package com.example.todo.toolcall.dto;

import com.example.todo.message.dto.MessageDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ToolCallActionResponseDto {
  private ToolCallDto toolCall;
  private MessageDto message;

  public static ToolCallActionResponseDto of(ToolCallDto toolCall, MessageDto message) {
    return new ToolCallActionResponseDto(toolCall, message);
  }
}
