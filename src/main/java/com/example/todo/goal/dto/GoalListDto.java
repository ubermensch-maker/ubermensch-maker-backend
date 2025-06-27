package com.example.todo.goal.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoalListDto {
  private int total;
  private List<GoalDto> items;
}
