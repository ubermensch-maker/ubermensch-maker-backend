package com.example.todo.api;

import com.example.todo.milestone.dto.MilestoneDto;
import com.example.todo.milestone.enums.MilestoneStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MilestoneApiTest {
    RestClient restClient = RestClient.create("http://localhost:8080");

    static final long TEST_USER_ID = 1L;
    static final long TEST_GOAL_ID = 1L;
    static final long TEST_MILESTONE_ID = 1L;

    @Test
    void createTest() {
        MilestoneDto response = create(TEST_USER_ID, new MilestoneCreateDto(
                TEST_GOAL_ID,
                "title",
                "description",
                Instant.parse("2025-04-27T00:00:00Z"),
                Instant.parse("2025-04-30T00:00:00Z")
        ));
        System.out.println("response = " + response);
    }

    MilestoneDto create(Long userId, MilestoneCreateDto request) {
        return restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/milestones")
                        .queryParam("userId", userId)
                        .build()
                )
                .body(request)
                .retrieve()
                .body(MilestoneDto.class);
    }

    @Test
    void readTest() {
        MilestoneDto response = read(TEST_MILESTONE_ID);
        System.out.println("response = " + response);
    }

    MilestoneDto read(Long milestoneId) {
        return restClient.get()
                .uri("/milestones/{milestoneId}", milestoneId)
                .retrieve()
                .body(MilestoneDto.class);
    }

    @Test
    void updateTest() {
        update(TEST_USER_ID, TEST_MILESTONE_ID, new MilestoneUpdateDto("new title", "new description", MilestoneStatus.IN_PROGRESS, null, null));
        MilestoneDto response = read(TEST_MILESTONE_ID);
        System.out.println("response = " + response);
    }

    void update(Long userId, Long milestoneId, MilestoneUpdateDto request) {
        restClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/milestones/{milestoneId}")
                        .queryParam("userId", userId)
                        .build(milestoneId)
                )
                .body(request)
                .retrieve()
                .body(MilestoneDto.class);
    }

    @Test
    void deleteTest() {
        delete(TEST_USER_ID, TEST_MILESTONE_ID);
        assertThrows(Exception.class, () -> read(TEST_MILESTONE_ID));
    }

    void delete(Long userId, Long milestoneId) {
        restClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/milestones/{milestoneId}")
                        .queryParam("userId", userId)
                        .build(milestoneId))
                .retrieve()
                .body(Void.class);
    }

    @Getter
    @AllArgsConstructor
    static class MilestoneCreateDto {
        private Long goalId;
        private String title;
        private String description;
        private Instant startAt;
        private Instant endAt;
    }

    @Getter
    @AllArgsConstructor
    static class MilestoneUpdateDto {
        private String title;
        private String description;
        private MilestoneStatus status;
        private Instant startAt;
        private Instant endAt;
    }
}
