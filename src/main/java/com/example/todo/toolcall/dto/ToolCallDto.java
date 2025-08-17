package com.example.todo.toolcall.dto;

import com.example.todo.toolcall.ToolCall;
import com.example.todo.toolcall.enums.ToolCallStatus;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ToolCallDto {
  private final UUID id;
  private final String functionName;
  private final Map<String, Object> arguments;
  private final Map<String, Object> result;
  private final ToolCallStatus status;
  private final String openaiToolCallId;
  private final Instant createdAt;
  private final Instant updatedAt;

  public ToolCallDto(ToolCall toolCall) {
    this.id = toolCall.getId();
    this.functionName = toolCall.getFunctionName();
    this.arguments = toolCall.getArguments();
    this.result = toolCall.getResult();
    this.status = toolCall.getStatus();
    this.openaiToolCallId = toolCall.getOpenaiToolCallId();
    this.createdAt = toolCall.getCreatedAt();
    this.updatedAt = toolCall.getUpdatedAt();
  }

  public static ToolCallDto from(ToolCall toolCall) {
    return new ToolCallDto(toolCall);
  }
}