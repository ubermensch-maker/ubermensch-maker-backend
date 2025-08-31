package com.example.todo.admin;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "prompt_templates")
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE prompt_templates SET deleted_at = NOW() WHERE id = ?")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromptTemplate {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(nullable = false)
  private Integer version;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private Map<String, Object> metadata;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  public static PromptTemplate create(String name, String content, Map<String, Object> metadata) {
    PromptTemplate promptTemplate = new PromptTemplate();
    promptTemplate.name = name;
    promptTemplate.content = content;
    promptTemplate.version = 1;
    promptTemplate.metadata = metadata;
    promptTemplate.createdAt = Instant.now();
    promptTemplate.updatedAt = promptTemplate.createdAt;
    return promptTemplate;
  }

  public void update(String name, String content, Map<String, Object> metadata) {
    if (name != null) this.name = name;
    if (content != null) this.content = content;
    if (metadata != null) this.metadata = metadata;
    this.version = this.version + 1;
    this.updatedAt = Instant.now();
  }
}
