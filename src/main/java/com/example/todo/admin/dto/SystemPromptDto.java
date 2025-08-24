package com.example.todo.admin.dto;

import com.example.todo.admin.SystemPrompt;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SystemPromptDto {
  private Long id;
  private String name;
  private String prompt;
  private Integer version;
  private Map<String, Object> metadata;
  private Instant createdAt;
  private Instant updatedAt;

  public static SystemPromptDto from(SystemPrompt systemPrompt) {
    return new SystemPromptDto(
        systemPrompt.getId(),
        systemPrompt.getName(),
        systemPrompt.getPrompt(),
        systemPrompt.getVersion(),
        systemPrompt.getMetadata(),
        systemPrompt.getCreatedAt(),
        systemPrompt.getUpdatedAt());
  }
}