package com.example.todo.usage;

import com.example.todo.message.Message;
import com.example.todo.message.enums.Model;
import com.example.todo.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "token_usage")
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE token_usage SET deleted_at = NOW() WHERE id = ?")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenUsage {

  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(columnDefinition = "uuid default uuid_generate_v7()")
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "message_id")
  private Message message;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Model model;

  @Column(name = "prompt_tokens", nullable = false)
  private Integer promptTokens;

  @Column(name = "completion_tokens", nullable = false)
  private Integer completionTokens;

  @Column(name = "total_tokens", nullable = false)
  private Integer totalTokens;

  @Column(name = "request_type")
  private String requestType; // "chat_completion", "chat_completion_with_tools", "title_generation"

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  private TokenUsage(
      User user,
      Message message,
      Model model,
      Integer promptTokens,
      Integer completionTokens,
      Integer totalTokens,
      String requestType) {
    this.user = user;
    this.message = message;
    this.model = model;
    this.promptTokens = promptTokens;
    this.completionTokens = completionTokens;
    this.totalTokens = totalTokens;
    this.requestType = requestType;
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
  }

  public static TokenUsage create(
      User user,
      Message message,
      Model model,
      Integer promptTokens,
      Integer completionTokens,
      Integer totalTokens,
      String requestType) {
    return new TokenUsage(
        user, message, model, promptTokens, completionTokens, totalTokens, requestType);
  }
}
