package com.example.todo.goal;

import com.example.todo.goal.dto.GoalCreateDto;
import com.example.todo.goal.dto.GoalDto;
import com.example.todo.goal.dto.GoalListDto;
import com.example.todo.goal.dto.GoalUpdateDto;
import com.example.todo.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GoalController {
  private final GoalService goalService;
  private final UserService userService;

  @PostMapping("/goals")
  public GoalDto create(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @RequestBody GoalCreateDto request) {
    Long userId = userService.getByEmail(principal.getUsername()).getId();
    return goalService.create(userId, request);
  }

  @GetMapping("/goals/{goalId}")
  public GoalDto get(@PathVariable Long goalId) {
    return goalService.get(goalId);
  }

  @GetMapping("/goals")
  public GoalListDto list(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
    Long userId = userService.getByEmail(principal.getUsername()).getId();
    return goalService.list(userId);
  }

  @PutMapping("/goals/{goalId}")
  public GoalDto update(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @PathVariable Long goalId,
      @RequestBody GoalUpdateDto request) {
    Long userId = userService.getByEmail(principal.getUsername()).getId();
    return goalService.update(userId, goalId, request);
  }

  @DeleteMapping("/goals/{goalId}")
  public void delete(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @PathVariable Long goalId) {
    Long userId = userService.getByEmail(principal.getUsername()).getId();
    goalService.delete(userId, goalId);
  }
}
