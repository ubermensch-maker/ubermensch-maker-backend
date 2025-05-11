package com.example.todo.api;

import com.example.todo.task.dto.TaskDto;
import com.example.todo.task.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TaskApiTest {
    RestClient restClient = RestClient.create("http://localhost:8080");

    static final long TEST_USER_ID = 1L;
    static final long TEST_GOAL_ID = 1L;
    static final long TEST_KPI_ID = 1L;
    static final long TEST_TASK_ID = 1L;

    @Test
    void createTest() {
        TaskDto response = create(new TaskCreateDto(
                TEST_USER_ID,
                TEST_GOAL_ID,
                TEST_KPI_ID,
                "title",
                "description",
                Instant.parse("2025-04-27T00:00:00Z"),
                Instant.parse("2025-04-30T00:00:00Z")
        ));
        System.out.println("response = " + response);
    }

    TaskDto create(TaskCreateDto request) {
        return restClient.post()
                .uri("/tasks")
                .body(request)
                .retrieve()
                .body(TaskDto.class);
    }

    @Test
    void readTest() {
        TaskDto response = read(TEST_TASK_ID);
        System.out.println("response = " + response);
    }

    TaskDto read(Long taskId) {
        return restClient.get()
                .uri("/tasks/{taskId}", taskId)
                .retrieve()
                .body(TaskDto.class);
    }

    @Test
    void updateTest() {
        update(TEST_TASK_ID, new TaskUpdateDto(TEST_USER_ID, "new title", "new description", TaskStatus.IN_PROGRESS, null, null));
        TaskDto response = read(TEST_TASK_ID);
        System.out.println("response = " + response);
    }

    void update(Long taskId, TaskUpdateDto request) {
        restClient.put()
                .uri("/tasks/{taskId}", taskId)
                .body(request)
                .retrieve()
                .body(TaskDto.class);
    }

    @Test
    void deleteTest() {
        delete(TEST_TASK_ID, TEST_USER_ID);
        assertThrows(Exception.class, () -> read(TEST_TASK_ID));
    }

    void delete(Long taskId, Long userId) {
        restClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/tasks/{taskId}")
                        .queryParam("userId", userId)
                        .build(taskId))
                .retrieve()
                .body(Void.class);
    }

    @Getter
    @AllArgsConstructor
    static class TaskCreateDto {
        private Long userId;
        private Long goalId;
        private Long kpiId;
        private String title;
        private String description;
        private Instant startAt;
        private Instant endAt;
    }


    @Getter
    @AllArgsConstructor
    static class TaskUpdateDto {
        private Long userId;
        private String title;
        private String description;
        private TaskStatus status;
        private Instant startAt;
        private Instant endAt;
    }
}
