package com.example.todo.toolcall.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ToolCallStatus {
  @JsonProperty("pending")
  PENDING,
  @JsonProperty("accepted")
  ACCEPTED,
  @JsonProperty("rejected")
  REJECTED
}