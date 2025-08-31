package com.example.todo.toolcall.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ToolCallSource {
  @JsonProperty("OPENAI")
  OPENAI,
  @JsonProperty("ANTHROPIC")
  ANTHROPIC,
  @JsonProperty("GOOGLE")
  GOOGLE,
  @JsonProperty("MCP")
  MCP,
  @JsonProperty("CUSTOM")
  CUSTOM
}
