package com.example.todo.goal.dto;

import com.example.todo.goal.Goal;
import com.example.todo.goal.enums.GoalStatus;
import java.time.Instant;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GoalDto {
  private Long id;
  private Long userId;
  private String title;
  private String description;
  private GoalStatus status;
  private Instant startAt;
  private Instant endAt;
  private Instant createdAt;
  private Instant updatedAt;

  public static GoalDto from(Goal goal) {
    GoalDto response = new GoalDto();
    response.id = goal.getId();
    response.userId = goal.getUser().getId();
    response.title = goal.getTitle();
    response.description = goal.getDescription();
    response.status = goal.getStatus();
    response.startAt = goal.getStartAt();
    response.endAt = goal.getEndAt();
    response.createdAt = goal.getCreatedAt();
    response.updatedAt = goal.getUpdatedAt();
    return response;
  }
}
