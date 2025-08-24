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
@Table(name = "system_prompts")
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE system_prompts SET deleted_at = NOW() WHERE id = ?")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SystemPrompt {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String prompt;

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

  public static SystemPrompt create(String name, String prompt, Map<String, Object> metadata) {
    SystemPrompt systemPrompt = new SystemPrompt();
    systemPrompt.name = name;
    systemPrompt.prompt = prompt;
    systemPrompt.version = 1;
    systemPrompt.metadata = metadata;
    systemPrompt.createdAt = Instant.now();
    systemPrompt.updatedAt = systemPrompt.createdAt;
    return systemPrompt;
  }

  public void update(String name, String prompt, Map<String, Object> metadata) {
    if (name != null) this.name = name;
    if (prompt != null) this.prompt = prompt;
    if (metadata != null) this.metadata = metadata;
    this.version = this.version + 1;
    this.updatedAt = Instant.now();
  }
}