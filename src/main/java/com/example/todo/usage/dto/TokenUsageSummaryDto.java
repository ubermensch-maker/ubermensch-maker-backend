package com.example.todo.usage.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class TokenUsageSummaryDto {
  private final Long totalTokens;
  private final Long totalThisMonth;
  private final List<TokenUsageDto> recentUsage;

  public TokenUsageSummaryDto(
      Long totalTokens, Long totalThisMonth, List<TokenUsageDto> recentUsage) {
    this.totalTokens = totalTokens;
    this.totalThisMonth = totalThisMonth;
    this.recentUsage = recentUsage;
  }
}
