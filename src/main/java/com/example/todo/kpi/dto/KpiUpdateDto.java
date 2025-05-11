package com.example.todo.kpi.dto;

import com.example.todo.kpi.enums.KpiStatus;
import lombok.Getter;

import java.time.Instant;

@Getter
public class KpiUpdateDto {
    private Long userId;
    private String title;
    private String description;
    private KpiStatus status;
    private Instant startAt;
    private Instant endAt;
}
