package com.example.todo.admin;

import com.example.todo.admin.dto.PromptTemplateDto;
import com.example.todo.admin.dto.PromptTemplateListDto;
import com.example.todo.admin.dto.PromptTemplateUpdateDto;
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
  private final PromptTemplateService promptTemplateService;
  private final UserService userService;

  @GetMapping("/admin/prompt-templates/{promptTemplateId}")
  public PromptTemplateDto getPromptTemplate(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @PathVariable Long promptTemplateId) {
    validateAdminAccess(principal);
    return promptTemplateService.get(promptTemplateId);
  }

  @GetMapping("/admin/prompt-templates")
  public PromptTemplateListDto listPromptTemplates(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
    validateAdminAccess(principal);
    return promptTemplateService.list();
  }

  @PutMapping("/admin/prompt-templates/{promptTemplateId}")
  public PromptTemplateDto updatePromptTemplate(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @PathVariable Long promptTemplateId,
      @RequestBody PromptTemplateUpdateDto request) {
    validateAdminAccess(principal);
    return promptTemplateService.update(promptTemplateId, request);
  }

  private void validateAdminAccess(org.springframework.security.core.userdetails.User principal) {
    UserDto user = userService.getByEmail(principal.getUsername());
    if (!UserRole.ADMIN.equals(user.getRole())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
    }
  }
}