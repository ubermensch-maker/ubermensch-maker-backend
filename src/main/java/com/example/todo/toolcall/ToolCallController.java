package com.example.todo.toolcall;

import com.example.todo.toolcall.dto.ToolCallActionDto;
import com.example.todo.toolcall.dto.ToolCallDto;
import com.example.todo.user.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tool-calls")
@RequiredArgsConstructor
public class ToolCallController {
  private final ToolCallService toolCallService;
  private final UserService userService;

  @PostMapping("/{toolCallId}/action")
  public ResponseEntity<ToolCallDto> executeAction(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @PathVariable UUID toolCallId,
      @RequestBody ToolCallActionDto request) {
    
    Long userId = userService.getByEmail(principal.getUsername()).getId();
    
    ToolCall toolCall = toolCallService.executeAction(userId, toolCallId, request);
    return ResponseEntity.ok(ToolCallDto.from(toolCall));
  }
}