package com.example.todo.api;

import com.example.todo.kpi.dto.KpiDto;
import com.example.todo.kpi.enums.KpiStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class KpiApiTest {
    RestClient restClient = RestClient.create("http://localhost:8080");

    static final long TEST_USER_ID = 1L;
    static final long TEST_GOAL_ID = 1L;
    static final long TEST_KPI_ID = 1L;

    @Test
    void createTest() {
        KpiDto response = create(new KpiCreateDto(
                TEST_USER_ID,
                TEST_GOAL_ID,
                "title",
                "description",
                Instant.parse("2025-04-27T00:00:00Z"),
                Instant.parse("2025-04-30T00:00:00Z")
        ));
        System.out.println("response = " + response);
    }

    KpiDto create(KpiCreateDto request) {
        return restClient.post()
                .uri("/kpis")
                .body(request)
                .retrieve()
                .body(KpiDto.class);
    }

    @Test
    void readTest() {
        KpiDto response = read(TEST_KPI_ID);
        System.out.println("response = " + response);
    }

    KpiDto read(Long kpiId) {
        return restClient.get()
                .uri("/kpis/{kpiId}", kpiId)
                .retrieve()
                .body(KpiDto.class);
    }

    @Test
    void updateTest() {
        update(TEST_KPI_ID, new KpiUpdateDto(TEST_USER_ID, "new title", "new description", KpiStatus.IN_PROGRESS, null, null));
        KpiDto response = read(TEST_KPI_ID);
        System.out.println("response = " + response);
    }

    void update(Long kpiId, KpiUpdateDto request) {
        restClient.put()
                .uri("/kpis/{kpiId}", kpiId)
                .body(request)
                .retrieve()
                .body(KpiDto.class);
    }

    @Test
    void deleteTest() {
        delete(TEST_KPI_ID, TEST_USER_ID);
        assertThrows(Exception.class, () -> read(TEST_KPI_ID));
    }

    void delete(Long kpiId, Long userId) {
        restClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/kpis/{kpiId}")
                        .queryParam("userId", userId)
                        .build(kpiId))
                .retrieve()
                .body(Void.class);
    }

    @Getter
    @AllArgsConstructor
    static class KpiCreateDto {
        private Long userId;
        private Long goalId;
        private String title;
        private String description;
        private Instant startAt;
        private Instant endAt;
    }

    @Getter
    @AllArgsConstructor
    static class KpiUpdateDto {
        private Long userId;
        private String title;
        private String description;
        private KpiStatus status;
        private Instant startAt;
        private Instant endAt;
    }
}
