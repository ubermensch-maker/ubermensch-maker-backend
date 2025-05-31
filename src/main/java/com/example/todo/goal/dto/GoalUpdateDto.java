package com.example.todo.goal.dto;

import com.example.todo.goal.enums.GoalStatus;
import lombok.Getter;

import java.time.Instant;

@Getter
public class GoalUpdateDto {
    private String title;
    private String description;
    private GoalStatus status;
    private Instant startAt;
    private Instant endAt;
}
