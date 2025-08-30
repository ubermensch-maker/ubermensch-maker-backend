package com.example.todo.toolcall;

import com.example.todo.message.MessageService;
import com.example.todo.message.dto.MessageDto;
import com.example.todo.toolcall.dto.ToolCallActionDto;
import com.example.todo.toolcall.dto.ToolCallActionResponseDto;
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
  private final MessageService messageService;
  private final UserService userService;

  @PostMapping("/{toolCallId}")
  public ResponseEntity<ToolCallActionResponseDto> executeAction(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @PathVariable UUID toolCallId,
      @RequestBody ToolCallActionDto request) {

    Long userId = userService.getByEmail(principal.getUsername()).getId();

    // Execute the tool action (accept/reject)
    ToolCall toolCall = toolCallService.executeAction(userId, toolCallId, request);

    // Generate summary message and send to OpenAI for response
    String toolResultMessage = toolCallService.generateToolResultMessage(toolCall);
    MessageDto responseMessage =
        messageService.processToolResult(userId, toolCall, toolResultMessage);

    return ResponseEntity.ok(
        ToolCallActionResponseDto.of(ToolCallDto.from(toolCall), responseMessage));
  }
}
