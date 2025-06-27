package com.example.todo.goal.dto;

import java.time.Instant;
import lombok.Getter;

@Getter
public class GoalCreateDto {
  private String title;
  private String description;
  private Instant startAt;
  private Instant endAt;
}
