package com.example.todo.milestone;

import com.example.todo.milestone.dto.MilestoneCreateDto;
import com.example.todo.milestone.dto.MilestoneDto;
import com.example.todo.milestone.dto.MilestoneListDto;
import com.example.todo.milestone.dto.MilestoneUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MilestoneController {
    private final MilestoneService milestoneService;

    @PostMapping("/milestones")
    public MilestoneDto create(@RequestParam Long userId, @RequestBody MilestoneCreateDto request) {
        return milestoneService.create(userId, request);
    }

    @GetMapping("/milestones/{milestoneId}")
    public MilestoneDto read(@PathVariable Long milestoneId) {
        return milestoneService.read(milestoneId);
    }

    @GetMapping("/milestones")
    public MilestoneListDto list(@RequestParam Long userId, @RequestParam(required = false) Long goalId) {
        return milestoneService.list(userId, goalId);
    }

    @PutMapping("/milestones/{milestoneId}")
    public MilestoneDto update(@RequestParam Long userId, @PathVariable Long milestoneId, @RequestBody MilestoneUpdateDto request) {
        return milestoneService.update(userId, milestoneId, request);
    }

    @DeleteMapping("/milestones/{milestoneId}")
    public void delete(@RequestParam Long userId, @PathVariable Long milestoneId) {
        milestoneService.delete(userId, milestoneId);
    }
}
