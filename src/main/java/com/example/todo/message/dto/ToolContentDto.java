package com.example.todo.message.dto;

import com.example.todo.message.enums.ContentType;
import java.util.Map;
import lombok.Getter;

@Getter
public class ToolContentDto extends ContentDto {
  private final String name;
  private final Map<String, Object> input;

  public ToolContentDto(String name, Map<String, Object> input) {
    super(ContentType.TOOL);
    this.name = name;
    this.input = input;
  }
}