package com.example.todo.message.dto;

import com.example.todo.message.enums.ContentType;
import lombok.Getter;

@Getter
public class TextContentDto extends ContentDto {
  private final String text;

  public TextContentDto(String text) {
    super(ContentType.TEXT);
    this.text = text;
  }
}