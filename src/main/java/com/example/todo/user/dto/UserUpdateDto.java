package com.example.todo.user.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserUpdateDto {
    private String email;
    private String password;
    private String name;
    private String timezone;
}
