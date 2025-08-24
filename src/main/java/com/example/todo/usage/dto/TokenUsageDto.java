package com.example.todo.usage.dto;

import com.example.todo.message.enums.Model;
import com.example.todo.usage.TokenUsage;
import java.time.Instant;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TokenUsageDto {
  private Long id;
  private Model model;
  private Integer inputTokens;
  private Integer outputTokens;
  private Integer totalTokens;
  private String requestType;
  private Instant createdAt;

  public static TokenUsageDto from(TokenUsage tokenUsage) {
    TokenUsageDto response = new TokenUsageDto();
    response.id = tokenUsage.getId();
    response.model = tokenUsage.getModel();
    response.inputTokens = tokenUsage.getInputTokens();
    response.outputTokens = tokenUsage.getOutputTokens();
    response.totalTokens = tokenUsage.getTotalTokens();
    response.requestType = tokenUsage.getRequestType();
    response.createdAt = tokenUsage.getCreatedAt();
    return response;
  }
}
