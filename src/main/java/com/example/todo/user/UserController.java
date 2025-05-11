package com.example.todo.user;

import com.example.todo.user.dto.UserCreateDto;
import com.example.todo.user.dto.UserUpdateDto;
import com.example.todo.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public UserDto create(@RequestBody UserCreateDto request) {
        return userService.create(request);
    }

    @GetMapping("/users/{userId}")
    public UserDto read(@PathVariable Long userId) {
        return userService.read(userId);
    }

    @GetMapping("/users")
    public List<UserDto> list() {
        return userService.list();
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
