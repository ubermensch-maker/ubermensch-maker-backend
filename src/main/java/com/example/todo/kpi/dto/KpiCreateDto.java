package com.example.todo.kpi.dto;

import lombok.Getter;

import java.time.Instant;

@Getter
public class KpiCreateDto {
    private Long userId;
    private Long goalId;
    private String title;
    private String description;
    private Instant startAt;
    private Instant endAt;
}
