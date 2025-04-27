package com.example.todo.dto.request;

import lombok.Getter;

import java.time.Instant;

@Getter
public class GoalCreateRequest {
    private Long userId;
    private String title;
    private String description;
    private Instant startAt;
    private Instant endAt;
}
