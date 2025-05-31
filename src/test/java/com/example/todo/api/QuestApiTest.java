package com.example.todo.api;

import com.example.todo.quest.dto.QuestDto;
import com.example.todo.quest.enums.QuestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class QuestApiTest {
    RestClient restClient = RestClient.create("http://localhost:8080");

    static final long TEST_USER_ID = 1L;
    static final long TEST_GOAL_ID = 1L;
    static final long TEST_MILESTONE_ID = 1L;
    static final long TEST_QUEST_ID = 1L;

    @Test
    void createTest() {
        QuestDto response = create(TEST_USER_ID, new QuestCreateDto(
                TEST_GOAL_ID,
                TEST_MILESTONE_ID,
                "title",
                "description",
                Instant.parse("2025-04-27T00:00:00Z"),
                Instant.parse("2025-04-30T00:00:00Z")
        ));
        System.out.println("response = " + response);
    }

    QuestDto create(Long userId, QuestCreateDto request) {
        return restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/quests")
                        .queryParam("userId", userId)
                        .build()
                )
                .body(request)
                .retrieve()
                .body(QuestDto.class);
    }

    @Test
    void readTest() {
        QuestDto response = read(TEST_QUEST_ID);
        System.out.println("response = " + response);
    }

    QuestDto read(Long questId) {
        return restClient.get()
                .uri("/quests/{questId}", questId)
                .retrieve()
                .body(QuestDto.class);
    }

    @Test
    void updateTest() {
        update(TEST_USER_ID, TEST_QUEST_ID, new QuestUpdateDto("new title", "new description", QuestStatus.IN_PROGRESS, null, null));
        QuestDto response = read(TEST_QUEST_ID);
        System.out.println("response = " + response);
    }

    void update(Long userId, Long questId, QuestUpdateDto request) {
        restClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/quests/{questId}")
                        .queryParam("userId", userId)
                        .build(questId)
                )
                .body(request)
                .retrieve()
                .body(QuestDto.class);
    }

    @Test
    void deleteTest() {
        delete(TEST_USER_ID, TEST_QUEST_ID);
        assertThrows(Exception.class, () -> read(TEST_QUEST_ID));
    }

    void delete(Long userId, Long questId) {
        restClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/quests/{questId}")
                        .queryParam("userId", userId)
                        .build(questId))
                .retrieve()
                .body(Void.class);
    }

    @Getter
    @AllArgsConstructor
    static class QuestCreateDto {
        private Long goalId;
        private Long milestoneId;
        private String title;
        private String description;
        private Instant startAt;
        private Instant endAt;
    }


    @Getter
    @AllArgsConstructor
    static class QuestUpdateDto {
        private String title;
        private String description;
        private QuestStatus status;
        private Instant startAt;
        private Instant endAt;
    }
}
