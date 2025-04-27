package com.example.todo.controller;

import com.example.todo.dto.request.TaskCreateRequest;
import com.example.todo.dto.request.TaskUpdateRequest;
import com.example.todo.dto.response.TaskResponse;
import com.example.todo.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/tasks")
    public TaskResponse create(@RequestBody TaskCreateRequest request) {
        return taskService.create(request);
    }

    @GetMapping("/tasks/{taskId}")
    public TaskResponse read(@PathVariable Long taskId) {
        return taskService.read(taskId);
    }

    @GetMapping("/tasks")
    public List<TaskResponse> list(@RequestParam Long userId) {
        return taskService.list(userId);
    }

    @PutMapping("/tasks/{taskId}")
    public TaskResponse update(@PathVariable Long taskId, @RequestBody TaskUpdateRequest request) {
        return taskService.update(taskId, request);
    }

    @DeleteMapping("/tasks/{taskId}")
    public void delete(@PathVariable Long taskId, @RequestParam Long userId) {
        taskService.delete(taskId, userId);
    }
}
