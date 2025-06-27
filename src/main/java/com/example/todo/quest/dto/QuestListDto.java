package com.example.todo.quest.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestListDto {
  private int total;
  private List<QuestDto> items;
}
