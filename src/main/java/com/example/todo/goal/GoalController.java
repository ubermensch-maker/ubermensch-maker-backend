package com.example.todo.goal;

import com.example.todo.goal.dto.GoalCreateDto;
import com.example.todo.goal.dto.GoalDto;
import com.example.todo.goal.dto.GoalListDto;
import com.example.todo.goal.dto.GoalUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/goals")
    public GoalDto create(@RequestParam Long userId, @RequestBody GoalCreateDto request) {
        return goalService.create(userId, request);
    }

    @GetMapping("/goals/{goalId}")
    public GoalDto read(@PathVariable Long goalId) {
        return goalService.read(goalId);
    }

    @GetMapping("/goals")
    public GoalListDto list(@RequestParam Long userId) {
        return goalService.list(userId);
    }

    @PutMapping("/goals/{goalId}")
    public GoalDto update(@RequestParam Long userId, @PathVariable Long goalId, @RequestBody GoalUpdateDto request) {
        return goalService.update(userId, goalId, request);
    }

    @DeleteMapping("/goals/{goalId}")
    public void delete(@RequestParam Long userId, @PathVariable Long goalId) {
        goalService.delete(userId, goalId);
    }
}
