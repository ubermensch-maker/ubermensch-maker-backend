package com.example.todo.controller;

import com.example.todo.dto.request.GoalCreateRequest;
import com.example.todo.dto.request.GoalUpdateRequest;
import com.example.todo.dto.response.GoalResponse;
import com.example.todo.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/goals")
    public GoalResponse create(@RequestBody GoalCreateRequest request) {
        return goalService.create(request);
    }

    @GetMapping("/goals/{goalId}")
    public GoalResponse read(@PathVariable Long goalId) {
        return goalService.read(goalId);
    }

    @GetMapping("/goals")
    public List<GoalResponse> list(@RequestParam Long userId) {
        return goalService.list(userId);
    }

    @PutMapping("/goals/{goalId}")
    public GoalResponse update(@PathVariable Long goalId, @RequestBody GoalUpdateRequest request) {
        return goalService.update(goalId, request);
    }

    @DeleteMapping("/goals/{goalId}")
    public void delete(@PathVariable Long goalId, @RequestParam Long userId) {
        goalService.delete(goalId, userId);
    }
}
