package com.example.todo.usage;

import com.example.todo.usage.dto.TokenUsageDto;
import com.example.todo.usage.dto.TokenUsageSummaryDto;
import com.example.todo.user.User;
import com.example.todo.user.UserRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TokenUsageService {
  private final TokenUsageRepository tokenUsageRepository;
  private final UserRepository userRepository;

  public TokenUsageSummaryDto getUserTokenUsageSummary(Long userId) {
    User user = userRepository
        .findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    // 전체 토큰 사용량
    Long totalTokens = tokenUsageRepository.getTotalTokensByUser(userId);
    if (totalTokens == null) totalTokens = 0L;

    // 이번 달 토큰 사용량
    LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
    Instant startOfMonth = firstDayOfMonth.atStartOfDay().toInstant(ZoneOffset.UTC);
    Long totalThisMonth = tokenUsageRepository.getTotalTokensByUserSince(userId, startOfMonth);
    if (totalThisMonth == null) totalThisMonth = 0L;

    // 최근 사용 내역 (최근 20개)
    Pageable pageable = PageRequest.of(0, 20);
    List<TokenUsage> recentUsageList = tokenUsageRepository.findByUserIdOrderByCreatedAtDesc(userId)
        .stream()
        .limit(20)
        .toList();
    
    List<TokenUsageDto> recentUsage = recentUsageList.stream()
        .map(TokenUsageDto::from)
        .toList();

    return new TokenUsageSummaryDto(totalTokens, totalThisMonth, recentUsage);
  }
}