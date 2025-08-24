package com.example.todo.admin.dto;

import java.util.Map;
import lombok.Getter;

@Getter
public class SystemPromptUpdateDto {
  private String name;
  private String prompt;
  private Map<String, Object> metadata;
}