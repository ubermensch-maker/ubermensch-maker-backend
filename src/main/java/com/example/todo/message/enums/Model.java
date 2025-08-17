package com.example.todo.message.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Model {
  @JsonProperty("gpt-4.1-nano")
  GPT_4_1_NANO,
  @JsonProperty("gpt-4o")
  GPT_4O,
}
