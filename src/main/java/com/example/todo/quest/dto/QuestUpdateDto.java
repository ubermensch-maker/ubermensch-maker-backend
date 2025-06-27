package com.example.todo.quest.dto;

import com.example.todo.quest.enums.QuestStatus;
import com.example.todo.quest.enums.QuestType;
import java.time.Instant;
import lombok.Getter;

@Getter
public class QuestUpdateDto {
  private String title;
  private String description;
  private QuestType type;
  private QuestStatus status;
  private Instant startAt;
  private Instant endAt;
}
