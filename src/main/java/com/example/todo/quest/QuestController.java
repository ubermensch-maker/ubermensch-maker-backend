package com.example.todo.quest;

import com.example.todo.quest.dto.QuestCreateDto;
import com.example.todo.quest.dto.QuestDto;
import com.example.todo.quest.dto.QuestListDto;
import com.example.todo.quest.dto.QuestUpdateDto;
import com.example.todo.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class QuestController {
  private final QuestService questService;
  private final UserService userService;

  @PostMapping("/quests")
  public QuestDto create(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @RequestBody QuestCreateDto request) {
    Long userId = userService.getByEmail(principal.getUsername()).getId();
    return questService.create(userId, request);
  }

  @GetMapping("/quests/{questId}")
  public QuestDto read(@PathVariable Long questId) {
    return questService.read(questId);
  }

  @GetMapping("/quests")
  public QuestListDto list(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @RequestParam(required = false) Long goalId,
      @RequestParam(required = false) Long milestoneId) {
    Long userId = userService.getByEmail(principal.getUsername()).getId();
    return questService.list(userId, goalId, milestoneId);
  }

  @PutMapping("/quests/{questId}")
  public QuestDto update(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @PathVariable Long questId,
      @RequestBody QuestUpdateDto request) {
    Long userId = userService.getByEmail(principal.getUsername()).getId();
    return questService.update(userId, questId, request);
  }

  @DeleteMapping("/quests/{questId}")
  public void delete(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @PathVariable Long questId) {
    Long userId = userService.getByEmail(principal.getUsername()).getId();
    questService.delete(userId, questId);
  }
}
