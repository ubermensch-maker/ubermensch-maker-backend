package com.example.todo.message.dto;

import com.example.todo.message.enums.ContentType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class ContentDto {
  private ContentType type;
  private String text;
}
