package com.example.todo.dto.response;

import com.example.todo.entity.Goal;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
public class GoalResponse {
    private Long id;
    private String title;
    private String description;
    private Instant startAt;
    private Instant endAt;
    private Instant createdAt;
    private Instant updatedAt;

    public static GoalResponse from(Goal goal) {
        GoalResponse response = new GoalResponse();
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
