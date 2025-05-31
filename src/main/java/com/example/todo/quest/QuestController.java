package com.example.todo.quest;

import com.example.todo.quest.dto.QuestCreateDto;
import com.example.todo.quest.dto.QuestDto;
import com.example.todo.quest.dto.QuestListDto;
import com.example.todo.quest.dto.QuestUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class QuestController {
    private final QuestService questService;

    @PostMapping("/quests")
    public QuestDto create(@RequestParam Long userId, @RequestBody QuestCreateDto request) {
        return questService.create(userId, request);
    }

    @GetMapping("/quests/{questId}")
    public QuestDto read(@PathVariable Long questId) {
        return questService.read(questId);
    }

    @GetMapping("/quests")
    public QuestListDto list(@RequestParam Long userId, @RequestParam(required = false) Long goalId, @RequestParam(required = false) Long milestoneId) {
        return questService.list(userId, goalId, milestoneId);
    }

    @PutMapping("/quests/{questId}")
    public QuestDto update(@RequestParam Long userId, @PathVariable Long questId, @RequestBody QuestUpdateDto request) {
        return questService.update(userId, questId, request);
    }

    @DeleteMapping("/quests/{questId}")
    public void delete(@RequestParam Long userId, @PathVariable Long questId) {
        questService.delete(userId, questId);
    }
}
