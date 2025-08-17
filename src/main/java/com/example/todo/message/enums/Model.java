package com.example.todo.message.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;

public enum Model {
  @JsonProperty("gpt-4.1-nano")
  GPT_4_1_NANO("gpt-4.1-nano"),

  @JsonProperty("gpt-4o")
  GPT_4O("gpt-4o");

  private final String value;

  Model(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static Model fromValue(String value) {
    return Arrays.stream(values())
        .filter(model -> model.value.equals(value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown model: " + value));
  }
}