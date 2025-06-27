package com.example.todo.milestone.dto;

import com.example.todo.milestone.enums.MilestoneStatus;
import java.time.Instant;
import lombok.Getter;

@Getter
public class MilestoneUpdateDto {
  private String title;
  private String description;
  private MilestoneStatus status;
  private Instant startAt;
  private Instant endAt;
}
