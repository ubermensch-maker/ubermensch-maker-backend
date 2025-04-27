package com.example.todo.api;

import com.example.todo.dto.response.GoalResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class GoalApiTest {
    RestClient restClient = RestClient.create("http://localhost:8080");

    static final long TEST_USER_ID = 1L;
    static final long TEST_GOAL_ID = 1L;

    @Test
    void createTest() {
        GoalResponse response = create(new GoalCreateRequest(
                1L,
                "title",
                "description",
                Instant.parse("2025-04-27T00:00:00Z"),
                Instant.parse("2025-04-30T00:00:00Z")
        ));
        System.out.println("response = " + response);
    }

    GoalResponse create(GoalCreateRequest request) {
        return restClient.post()
                .uri("/goals")
                .body(request)
                .retrieve()
                .body(GoalResponse.class);
    }

    @Test
    void readTest() {
        GoalResponse response = read(TEST_GOAL_ID);
        System.out.println("response = " + response);
    }

    GoalResponse read(Long goalId) {
        return restClient.get()
                .uri("/goals/{goalId}", goalId)
                .retrieve()
                .body(GoalResponse.class);
    }

    @Test
    void updateTest() {
        update(TEST_GOAL_ID, new GoalUpdateRequest(TEST_USER_ID, "new title", null, null, null));
        GoalResponse response = read(TEST_USER_ID);
        System.out.println("response = " + response);
    }

    void update(Long goalId, GoalUpdateRequest request) {
        restClient.put()
                .uri("/goals/{goalId}", goalId)
                .body(request)
                .retrieve()
                .body(GoalResponse.class);
    }

    @Test
    void deleteTest() {
        delete(TEST_GOAL_ID, TEST_USER_ID);
        assertThrows(Exception.class, () -> read(TEST_GOAL_ID));
    }

    void delete(Long goalId, Long userId) {
        restClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/goals/{goalId}")
                        .queryParam("userId", userId)
                        .build(goalId))
                .retrieve()
                .body(Void.class);
    }

    @Getter
    @AllArgsConstructor
    static class GoalCreateRequest {
        private Long userId;
        private String title;
        private String description;
        private Instant startAt;
        private Instant endAt;
    }

    @Getter
    @AllArgsConstructor
    static class GoalUpdateRequest {
        private Long userId;
        private String title;
        private String description;
        private Instant startAt;
        private Instant endAt;
    }
}
