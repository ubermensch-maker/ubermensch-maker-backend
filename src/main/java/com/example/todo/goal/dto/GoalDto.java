package com.example.todo.goal.dto;

import com.example.todo.goal.Goal;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
public class GoalDto {
    private Long id;
    private String title;
    private String description;
    private Instant startAt;
    private Instant endAt;
    private Instant createdAt;
    private Instant updatedAt;

    public static GoalDto from(Goal goal) {
        GoalDto response = new GoalDto();
        response.id = goal.getId();
        response.title = goal.getTitle();
        response.description = goal.getDescription();
        response.startAt = goal.getStartAt();
        response.endAt = goal.getEndAt();
        response.createdAt = goal.getCreatedAt();
        response.updatedAt = goal.getUpdatedAt();
        return response;
    }
}
