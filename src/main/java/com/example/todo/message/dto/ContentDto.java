package com.example.todo.message.dto;

import com.example.todo.message.enums.ContentType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = TextContentDto.class, name = "text"),
  @JsonSubTypes.Type(value = ToolContentDto.class, name = "tool")
})
public abstract class ContentDto {
  private ContentType type;

  protected ContentDto(ContentType type) {
    this.type = type;
  }
}