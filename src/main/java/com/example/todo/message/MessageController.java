package com.example.todo.message;

import com.example.todo.message.dto.MessageCreateDto;
import com.example.todo.message.dto.MessageDto;
import com.example.todo.message.dto.MessageListDto;
import com.example.todo.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MessageController {
  private final MessageService messageService;
  private final UserService userService;

  @PostMapping("/messages")
  public MessageDto create(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @RequestBody MessageCreateDto request) {
    Long userId = userService.getByEmail(principal.getUsername()).getId();
    return messageService.create(userId, request);
  }

  @GetMapping("/messages/{messageId}")
  public MessageDto read(@PathVariable Long messageId) {
    return messageService.read(messageId);
  }

  @GetMapping("/messages")
  public MessageListDto list(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @RequestParam Long conversationId) {
    Long userId = userService.getByEmail(principal.getUsername()).getId();
    return messageService.list(userId, conversationId);
  }

  @DeleteMapping("/messages/{messageId}")
  public void delete(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @PathVariable Long messageId) {
    Long userId = userService.getByEmail(principal.getUsername()).getId();
    messageService.delete(userId, messageId);
  }
}
