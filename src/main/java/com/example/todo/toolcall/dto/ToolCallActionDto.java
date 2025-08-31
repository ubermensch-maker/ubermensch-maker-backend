package com.example.todo.toolcall.dto;

import java.util.Map;
import lombok.Getter;

@Getter
public class ToolCallActionDto {
  private String action; // "accept" or "reject"
  private Map<String, Object> arguments; // optional override arguments for accept action
}
