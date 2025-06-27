package com.example.todo.milestone.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MilestoneListDto {
  private int total;
  private List<MilestoneDto> items;
}
