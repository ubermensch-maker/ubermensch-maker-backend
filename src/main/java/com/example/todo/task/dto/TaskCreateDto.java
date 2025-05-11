package com.example.todo.task.dto;

import lombok.Getter;

import java.time.Instant;

@Getter
public class TaskCreateDto {
    private Long userId;
    private Long goalId;
    private String title;
    private String description;
    private Instant startAt;
    private Instant endAt;
}
