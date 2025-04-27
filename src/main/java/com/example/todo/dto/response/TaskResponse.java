package com.example.todo.dto.response;

import com.example.todo.entity.Task;
import com.example.todo.entity.TaskStatus;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Instant startAt;
    private Instant endAt;
    private Instant createdAt;
    private Instant updatedAt;

    public static TaskResponse from(Task task) {
        TaskResponse response = new TaskResponse();
        response.id = task.getId();
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
