package com.example.todo.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(exclude = "password")
public class UserCreateRequest {
    private String email;
    private String password;
    private String name;
    private String timezone;
}
