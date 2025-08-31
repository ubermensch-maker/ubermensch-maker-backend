package com.example.todo.admin.dto;

import java.util.Map;
import lombok.Getter;

@Getter
public class PromptTemplateUpdateDto {
  private String name;
  private String content;
  private Map<String, Object> metadata;
}
