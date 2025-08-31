package com.example.todo.api;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.todo.goal.dto.GoalDto;
import com.example.todo.goal.enums.GoalStatus;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class GoalApiTest {
  RestClient restClient = RestClient.create("http://localhost:8080");

  static final long TEST_USER_ID = 1L;
  static final long TEST_GOAL_ID = 1L;

  @Test
  void createTest() {
    GoalDto response =
        create(
            TEST_USER_ID,
            new GoalCreateDto(
                "title",
                "description",
                Instant.parse("2025-04-27T00:00:00Z"),
                Instant.parse("2025-04-30T00:00:00Z")));
    System.out.println("response = " + response);
  }

  GoalDto create(Long userId, GoalCreateDto request) {
    return restClient
        .post()
        .uri(uriBuilder -> uriBuilder.path("/goals").queryParam("userId", userId).build())
        .body(request)
        .retrieve()
        .body(GoalDto.class);
  }

  @Test
  void getTest() {
    GoalDto response = get(TEST_GOAL_ID);
    System.out.println("response = " + response);
  }

  GoalDto get(Long goalId) {
    return restClient.get().uri("/goals/{goalId}", goalId).retrieve().body(GoalDto.class);
  }

  @Test
  void updateTest() {
    update(
        TEST_USER_ID,
        TEST_GOAL_ID,
        new GoalUpdateDto("new title", "new description", GoalStatus.IN_PROGRESS, null, null));
    GoalDto response = get(TEST_GOAL_ID);
    System.out.println("response = " + response);
  }

  void update(Long userId, Long goalId, GoalUpdateDto request) {
    restClient
        .put()
        .uri(
            uriBuilder ->
                uriBuilder.path("/goals/{goalId}").queryParam("userId", userId).build(goalId))
        .body(request)
        .retrieve()
        .body(GoalDto.class);
  }

  @Test
  void deleteTest() {
    delete(TEST_USER_ID, TEST_GOAL_ID);
    assertThrows(Exception.class, () -> get(TEST_GOAL_ID));
  }

  void delete(Long userId, Long goalId) {
    restClient
        .delete()
        .uri(
            uriBuilder ->
                uriBuilder.path("/goals/{goalId}").queryParam("userId", userId).build(goalId))
        .retrieve()
        .body(Void.class);
  }

  @Getter
  @AllArgsConstructor
  static class GoalCreateDto {
    private String title;
    private String description;
    private Instant startAt;
    private Instant endAt;
  }

  @Getter
  @AllArgsConstructor
  static class GoalUpdateDto {
    private String title;
    private String description;
    private GoalStatus status;
    private Instant startAt;
    private Instant endAt;
  }
}
