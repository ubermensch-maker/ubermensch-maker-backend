package com.example.todo.milestone.dto;

import java.time.Instant;
import lombok.Getter;

@Getter
public class MilestoneCreateDto {
  private Long goalId;
  private String title;
  private String description;
  private Instant startAt;
  private Instant endAt;
}
