package com.example.todo.dto.request;

import com.example.todo.entity.TaskStatus;
import lombok.Getter;

import java.time.Instant;

@Getter
public class TaskUpdateRequest {
    private Long userId;
    private String title;
    private String description;
    private TaskStatus status;
    private Instant startAt;
    private Instant endAt;
}
