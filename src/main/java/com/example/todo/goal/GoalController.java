package com.example.todo.goal;

import com.example.todo.goal.dto.GoalCreateDto;
import com.example.todo.goal.dto.GoalDto;
import com.example.todo.goal.dto.GoalUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/goals")
    public GoalDto create(@RequestBody GoalCreateDto request) {
        return goalService.create(request);
    }

    @GetMapping("/goals/{goalId}")
    public GoalDto read(@PathVariable Long goalId) {
        return goalService.read(goalId);
    }

    @GetMapping("/goals")
    public List<GoalDto> list(@RequestParam Long userId) {
        return goalService.list(userId);
    }

    @PutMapping("/goals/{goalId}")
    public GoalDto update(@PathVariable Long goalId, @RequestBody GoalUpdateDto request) {
        return goalService.update(goalId, request);
    }

    @DeleteMapping("/goals/{goalId}")
    public void delete(@PathVariable Long goalId, @RequestParam Long userId) {
        goalService.delete(goalId, userId);
    }
}
