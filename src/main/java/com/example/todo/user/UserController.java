package com.example.todo.user;

import com.example.todo.user.dto.UserDto;
import com.example.todo.user.dto.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users/{userId}")
    public UserDto read(@PathVariable Long userId) {
        return userService.read(userId);
    }

    @PutMapping("/users/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody UserUpdateDto request) {
        return userService.update(userId, request);
    }

    @DeleteMapping("/users/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
