package com.example.todo.goal.dto;

import lombok.Getter;

import java.time.Instant;

@Getter
public class GoalUpdateDto {
    private Long userId;
    private String title;
    private String description;
    private Instant startAt;
    private Instant endAt;
}
