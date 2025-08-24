package com.example.todo.admin.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SystemPromptListDto {
  private int total;
  private List<SystemPromptDto> items;
}
