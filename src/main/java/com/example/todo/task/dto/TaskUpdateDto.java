package com.example.todo.task.dto;

import com.example.todo.task.enums.TaskStatus;
import lombok.Getter;

import java.time.Instant;

@Getter
public class TaskUpdateDto {
    private Long userId;
    private Long goalId;
    private Long kpiId;
    private String title;
    private String description;
    private TaskStatus status;
    private Instant startAt;
    private Instant endAt;
}
