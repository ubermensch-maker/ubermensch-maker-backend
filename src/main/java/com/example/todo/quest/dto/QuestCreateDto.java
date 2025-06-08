package com.example.todo.quest.dto;

import com.example.todo.quest.enums.QuestType;
import lombok.Getter;

import java.time.Instant;

@Getter
public class QuestCreateDto {
    private Long goalId;
    private Long milestoneId;
    private String title;
    private String description;
    private QuestType type;
    private Instant startAt;
    private Instant endAt;
}
