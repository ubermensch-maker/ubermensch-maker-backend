package com.example.todo.quest.dto;

import com.example.todo.quest.enums.QuestStatus;
import lombok.Getter;

import java.time.Instant;

@Getter
public class QuestUpdateDto {
    private String title;
    private String description;
    private QuestStatus status;
    private Instant startAt;
    private Instant endAt;
}
