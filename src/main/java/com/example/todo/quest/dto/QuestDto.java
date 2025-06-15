package com.example.todo.quest.dto;

import com.example.todo.quest.Quest;
import com.example.todo.quest.enums.QuestStatus;
import com.example.todo.quest.enums.QuestType;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
public class QuestDto {
    private Long id;
    private Long userId;
    private Long goalId;
    private Long milestoneId;
    private String title;
    private String description;
    private QuestType type;
    private QuestStatus status;
    private Instant startAt;
    private Instant endAt;
    private Instant createdAt;
    private Instant updatedAt;

    public static QuestDto from(Quest quest) {
        QuestDto response = new QuestDto();
        response.id = quest.getId();
        response.userId = quest.getUser().getId();
        response.goalId = quest.getGoal() != null ? quest.getGoal().getId() : null;
        response.milestoneId = quest.getMilestone() != null ? quest.getMilestone().getId() : null;
        response.title = quest.getTitle();
        response.description = quest.getDescription();
        response.type = quest.getType();
        response.status = quest.getStatus();
        response.startAt = quest.getStartAt();
        response.endAt = quest.getEndAt();
        response.createdAt = quest.getCreatedAt();
        response.updatedAt = quest.getUpdatedAt();
        return response;
    }
}
