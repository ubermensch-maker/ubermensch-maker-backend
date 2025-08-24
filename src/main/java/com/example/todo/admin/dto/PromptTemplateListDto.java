package com.example.todo.admin.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PromptTemplateListDto {
  private int total;
  private List<PromptTemplateDto> items;
}
