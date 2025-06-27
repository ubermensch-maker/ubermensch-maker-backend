package com.example.todo.user.dto;

import com.example.todo.user.User;
import com.example.todo.user.enums.UserRole;
import java.time.Instant;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserDto {
  private Long id;
  private String email;
  private String name;
  private UserRole role;
  private Instant createdAt;
  private Instant updatedAt;

  public static UserDto from(User user) {
    UserDto response = new UserDto();
    response.id = user.getId();
    response.email = user.getEmail();
    response.name = user.getName();
    response.role = user.getRole();
    response.createdAt = user.getCreatedAt();
    response.updatedAt = user.getUpdatedAt();
    return response;
  }
}
