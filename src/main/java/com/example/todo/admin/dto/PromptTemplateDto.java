package com.example.todo.admin.dto;

import com.example.todo.admin.PromptTemplate;
import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PromptTemplateDto {
  private Long id;
  private String name;
  private String content;
  private Integer version;
  private Map<String, Object> metadata;
  private Instant createdAt;
  private Instant updatedAt;

  public static PromptTemplateDto from(PromptTemplate promptTemplate) {
    PromptTemplateDto response = new PromptTemplateDto();
    response.id = promptTemplate.getId();
    response.name = promptTemplate.getName();
    response.content = promptTemplate.getContent();
    response.version = promptTemplate.getVersion();
    response.metadata = promptTemplate.getMetadata();
    response.createdAt = promptTemplate.getCreatedAt();
    response.updatedAt = promptTemplate.getUpdatedAt();
    return response;
  }
}
