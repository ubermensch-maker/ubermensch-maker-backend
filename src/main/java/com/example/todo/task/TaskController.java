package com.example.todo.task;

import com.example.todo.task.dto.TaskCreateDto;
import com.example.todo.task.dto.TaskDto;
import com.example.todo.task.dto.TaskUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/tasks")
    public TaskDto create(@RequestBody TaskCreateDto request) {
        return taskService.create(request);
    }

    @GetMapping("/tasks/{taskId}")
    public TaskDto read(@PathVariable Long taskId) {
        return taskService.read(taskId);
    }

    @GetMapping("/tasks")
    public List<TaskDto> list(@RequestParam Long userId, @RequestParam(required = false) Long goalId, @RequestParam(required = false) Long kpiId) {
        return taskService.list(userId, goalId, kpiId);
    }

    @PutMapping("/tasks/{taskId}")
    public TaskDto update(@PathVariable Long taskId, @RequestBody TaskUpdateDto request) {
        return taskService.update(taskId, request);
    }

    @DeleteMapping("/tasks/{taskId}")
    public void delete(@PathVariable Long taskId, @RequestParam Long userId) {
        taskService.delete(taskId, userId);
    }
}
