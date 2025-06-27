package com.example.todo.milestone.dto;

import com.example.todo.milestone.Milestone;
import com.example.todo.milestone.enums.MilestoneStatus;
import java.time.Instant;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MilestoneDto {
  private Long id;
  private Long userId;
  private Long goalId;
  private String title;
  private String description;
  private MilestoneStatus status;
  private Instant startAt;
  private Instant endAt;
  private Instant createdAt;
  private Instant updatedAt;

  public static MilestoneDto from(Milestone milestone) {
    MilestoneDto response = new MilestoneDto();
    response.id = milestone.getId();
    response.userId = milestone.getUser().getId();
    response.goalId = milestone.getGoal().getId();
    response.title = milestone.getTitle();
    response.description = milestone.getDescription();
    response.status = milestone.getStatus();
    response.startAt = milestone.getStartAt();
    response.endAt = milestone.getEndAt();
    response.createdAt = milestone.getCreatedAt();
    response.updatedAt = milestone.getUpdatedAt();
    return response;
  }
}
