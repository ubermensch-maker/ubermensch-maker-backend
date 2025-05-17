package com.example.todo.task.dto;

import com.example.todo.task.Task;
import com.example.todo.task.enums.TaskStatus;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
public class TaskDto {
    private Long id;
    private Long userId;
    private Long goalId;
    private Long kpiId;
    private String title;
    private String description;
    private TaskStatus status;
    private Instant startAt;
    private Instant endAt;
    private Instant createdAt;
    private Instant updatedAt;

    public static TaskDto from(Task task) {
        TaskDto response = new TaskDto();
        response.id = task.getId();
        response.userId = task.getUser().getId();
        response.goalId = task.getGoal().getId();
        response.kpiId = task.getKpi().getId();
        response.title = task.getTitle();
        response.description = task.getDescription();
        response.status = task.getStatus();
        response.startAt = task.getStartAt();
        response.endAt = task.getEndAt();
        response.createdAt = task.getCreatedAt();
        response.updatedAt = task.getUpdatedAt();
        return response;
    }
}
