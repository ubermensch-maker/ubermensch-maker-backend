package com.example.todo.dto.response;

import com.example.todo.entity.User;
import com.example.todo.entity.UserRole;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private UserRole role;
    private String timezone;
    private Instant createdAt;
    private Instant updatedAt;

    public static UserResponse from(User user) {
        UserResponse response = new UserResponse();
        response.id = user.getId();
        response.email = user.getEmail();
        response.name = user.getName();
        response.role = user.getRole();
        response.timezone = user.getTimezone();
        response.createdAt = user.getCreatedAt();
        response.updatedAt = user.getUpdatedAt();
        return response;
    }
}
