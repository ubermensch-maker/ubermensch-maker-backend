package com.example.todo.user.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(exclude = "password")
public class UserCreateDto {
    private String email;
    private String password;
    private String name;
    private String timezone;
}
