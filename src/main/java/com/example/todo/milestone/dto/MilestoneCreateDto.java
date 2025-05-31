package com.example.todo.milestone.dto;

import lombok.Getter;

import java.time.Instant;

@Getter
public class MilestoneCreateDto {
    private Long goalId;
    private String title;
    private String description;
    private Instant startAt;
    private Instant endAt;
}
