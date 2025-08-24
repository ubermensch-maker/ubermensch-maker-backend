package com.example.todo.admin;

import com.example.todo.admin.dto.SystemPromptDto;
import com.example.todo.admin.dto.SystemPromptListDto;
import com.example.todo.admin.dto.SystemPromptUpdateDto;
import com.example.todo.user.UserService;
import com.example.todo.user.dto.UserDto;
import com.example.todo.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class AdminController {
  private final SystemPromptService systemPromptService;
  private final UserService userService;

  @GetMapping("/admin/system-prompts/{systemPromptId}")
  public SystemPromptDto getSystemPrompt(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @PathVariable Long systemPromptId) {
    validateAdminAccess(principal);
    return systemPromptService.get(systemPromptId);
  }

  @GetMapping("/admin/system-prompts")
  public SystemPromptListDto listSystemPrompts(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
    validateAdminAccess(principal);
    return systemPromptService.list();
  }

  @PutMapping("/admin/system-prompts/{systemPromptId}")
  public SystemPromptDto updateSystemPrompt(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @PathVariable Long systemPromptId,
      @RequestBody SystemPromptUpdateDto request) {
    validateAdminAccess(principal);
    return systemPromptService.update(systemPromptId, request);
  }

  private void validateAdminAccess(org.springframework.security.core.userdetails.User principal) {
    UserDto user = userService.getByEmail(principal.getUsername());
    if (!UserRole.ADMIN.equals(user.getRole())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
    }
  }
}