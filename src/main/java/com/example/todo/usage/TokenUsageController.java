package com.example.todo.usage;

import com.example.todo.usage.dto.TokenUsageSummaryDto;
import com.example.todo.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/token-usage")
@RequiredArgsConstructor
public class TokenUsageController {
  private final TokenUsageService tokenUsageService;
  private final UserService userService;

  @GetMapping("/summary")
  public ResponseEntity<TokenUsageSummaryDto> getUserTokenUsageSummary(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

    Long userId = userService.getByEmail(principal.getUsername()).getId();
    TokenUsageSummaryDto summary = tokenUsageService.getUserTokenUsageSummary(userId);

    return ResponseEntity.ok(summary);
  }
}
