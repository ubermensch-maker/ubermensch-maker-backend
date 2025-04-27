package com.example.todo.controller;

import com.example.todo.dto.request.UserCreateRequest;
import com.example.todo.dto.request.UserUpdateRequest;
import com.example.todo.dto.response.UserResponse;
import com.example.todo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public UserResponse create(@RequestBody UserCreateRequest request) {
        return userService.create(request);
    }

    @GetMapping("/users/{userId}")
    public UserResponse findOne(@PathVariable Long userId) {
        return userService.findOne(userId);
    }

    @GetMapping("/users")
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @PutMapping("/users/{userId}")
    public UserResponse update(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        return userService.update(userId, request);
    }

    @DeleteMapping("/users/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
