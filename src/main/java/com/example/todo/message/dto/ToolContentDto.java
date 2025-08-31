package com.example.todo.message.dto;

import com.example.todo.message.enums.ContentType;
import com.example.todo.toolcall.enums.ToolCallStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ToolContentDto extends ContentDto {
  private final String name;
  private final Map<String, Object> args;
  private final UUID toolCallId;
  private final ToolCallStatus status;
  private final String sourceCallId;

  @JsonCreator
  public ToolContentDto(
      @JsonProperty("name") String name,
      @JsonProperty("args") Map<String, Object> args,
      @JsonProperty("toolCallId") UUID toolCallId,
      @JsonProperty("status") ToolCallStatus status,
      @JsonProperty("sourceCallId") String sourceCallId) {
    super(ContentType.TOOL);
    this.name = name;
    this.args = args;
    this.toolCallId = toolCallId;
    this.status = status;
    this.sourceCallId = sourceCallId;
  }

  public ToolContentDto(String name, Map<String, Object> args) {
    super(ContentType.TOOL);
    this.name = name;
    this.args = args;
    this.toolCallId = null;
    this.status = null;
    this.sourceCallId = null;
  }
}
