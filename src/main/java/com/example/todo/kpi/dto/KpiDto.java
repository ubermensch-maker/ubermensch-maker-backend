package com.example.todo.kpi.dto;

import com.example.todo.kpi.Kpi;
import com.example.todo.kpi.enums.KpiStatus;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
public class KpiDto {
    private Long id;
    private String title;
    private String description;
    private KpiStatus status;
    private Instant startAt;
    private Instant endAt;
    private Instant createdAt;
    private Instant updatedAt;

    public static KpiDto from(Kpi kpi) {
        KpiDto response = new KpiDto();
        response.id = kpi.getId();
        response.title = kpi.getTitle();
        response.description = kpi.getDescription();
        response.status = kpi.getStatus();
        response.startAt = kpi.getStartAt();
        response.endAt = kpi.getEndAt();
        response.createdAt = kpi.getCreatedAt();
        response.updatedAt = kpi.getUpdatedAt();
        return response;
    }
}
