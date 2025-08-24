package com.example.todo.usage.dto;

import com.example.todo.message.enums.Model;
import com.example.todo.usage.TokenUsage;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class TokenUsageDto {
  private final UUID id;
  private final Model model;
  private final Integer promptTokens;
  private final Integer completionTokens;
  private final Integer totalTokens;
  private final String requestType;
  private final Instant createdAt;

  public TokenUsageDto(TokenUsage tokenUsage) {
    this.id = tokenUsage.getId();
    this.model = tokenUsage.getModel();
    this.promptTokens = tokenUsage.getPromptTokens();
    this.completionTokens = tokenUsage.getCompletionTokens();
    this.totalTokens = tokenUsage.getTotalTokens();
    this.requestType = tokenUsage.getRequestType();
    this.createdAt = tokenUsage.getCreatedAt();
  }

  public static TokenUsageDto from(TokenUsage tokenUsage) {
    return new TokenUsageDto(tokenUsage);
  }
}