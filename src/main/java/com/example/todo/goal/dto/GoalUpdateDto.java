package com.example.todo.goal.dto;

import com.example.todo.goal.enums.GoalStatus;
import java.time.Instant;
import lombok.Getter;

@Getter
public class GoalUpdateDto {
  private String title;
  private String description;
  private GoalStatus status;
  private Instant startAt;
  private Instant endAt;
}
