package com.example.todo.milestone;

import com.example.todo.milestone.dto.MilestoneCreateDto;
import com.example.todo.milestone.dto.MilestoneDto;
import com.example.todo.milestone.dto.MilestoneListDto;
import com.example.todo.milestone.dto.MilestoneUpdateDto;
import com.example.todo.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MilestoneController {
  private final MilestoneService milestoneService;
  private final UserService userService;

  @PostMapping("/milestones")
  public MilestoneDto create(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @RequestBody MilestoneCreateDto request) {
    Long userId = userService.getByEmail(principal.getUsername()).getId();
    return milestoneService.create(userId, request);
  }

  @GetMapping("/milestones/{milestoneId}")
  public MilestoneDto get(@PathVariable Long milestoneId) {
    return milestoneService.get(milestoneId);
  }

  @GetMapping("/milestones")
  public MilestoneListDto list(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @RequestParam(required = false) Long goalId) {
    Long userId = userService.getByEmail(principal.getUsername()).getId();
    return milestoneService.list(userId, goalId);
  }

  @PutMapping("/milestones/{milestoneId}")
  public MilestoneDto update(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @PathVariable Long milestoneId,
      @RequestBody MilestoneUpdateDto request) {
    Long userId = userService.getByEmail(principal.getUsername()).getId();
    return milestoneService.update(userId, milestoneId, request);
  }

  @DeleteMapping("/milestones/{milestoneId}")
  public void delete(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @PathVariable Long milestoneId) {
    Long userId = userService.getByEmail(principal.getUsername()).getId();
    milestoneService.delete(userId, milestoneId);
  }
}
