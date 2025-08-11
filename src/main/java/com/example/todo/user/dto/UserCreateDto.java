package com.example.todo.user.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserCreateDto {
  private String email;
  private String name;
}
