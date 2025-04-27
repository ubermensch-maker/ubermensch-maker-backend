package com.example.todo.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserUpdateRequest {
    private String email;
    private String password;
    private String name;
    private String timezone;
}
