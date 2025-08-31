package com.example.todo.user;

import com.example.todo.user.dto.UserDto;
import com.example.todo.user.dto.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping("/users")
  public UserDto get(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
    Long userId = userService.getByEmail(principal.getUsername()).getId();
    return userService.get(userId);
  }

  @PutMapping("/users/profile")
  public UserDto updateProfile(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @RequestBody UserUpdateDto request) {
    Long userId = userService.getByEmail(principal.getUsername()).getId();
    return userService.updateProfile(userId, request.getName());
  }

  @DeleteMapping("/users")
  public void delete(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
    Long userId = userService.getByEmail(principal.getUsername()).getId();
    userService.delete(userId);
  }
}
