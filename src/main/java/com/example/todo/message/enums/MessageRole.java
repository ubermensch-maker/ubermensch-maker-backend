package com.example.todo.message.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MessageRole {
  @JsonProperty("user")
  USER,
  @JsonProperty("assistant")
  ASSISTANT,
  @JsonProperty("system")
  SYSTEM,
  @JsonProperty("developer")
  DEVELOPER
}
