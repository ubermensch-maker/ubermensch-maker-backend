package com.example.todo.toolcall;

import com.example.todo.message.Message;
import com.example.todo.toolcall.enums.ToolCallSource;
import com.example.todo.toolcall.enums.ToolCallStatus;
import com.example.todo.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "tool_calls")
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE tool_calls SET deleted_at = NOW() WHERE id = ?")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ToolCall {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "message_id", nullable = false)
  private Message message;

  @Column(name = "tool_name", nullable = false)
  private String toolName;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private Map<String, Object> arguments;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private Map<String, Object> result;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ToolCallStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ToolCallSource source;

  @Column(name = "source_call_id")
  private String sourceCallId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "source_metadata", columnDefinition = "jsonb")
  private Map<String, Object> sourceMetadata;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  private ToolCall(
      User user,
      Message message,
      String toolName,
      Map<String, Object> arguments,
      ToolCallSource source,
      String sourceCallId,
      Map<String, Object> sourceMetadata) {
    this.user = user;
    this.message = message;
    this.toolName = toolName;
    this.arguments = arguments;
    this.source = source;
    this.sourceCallId = sourceCallId;
    this.sourceMetadata = sourceMetadata;
    this.status = ToolCallStatus.PENDING;
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
  }

  public static ToolCall create(
      User user,
      Message message,
      String toolName,
      Map<String, Object> arguments,
      ToolCallSource source,
      String sourceCallId,
      Map<String, Object> sourceMetadata) {
    return new ToolCall(user, message, toolName, arguments, source, sourceCallId, sourceMetadata);
  }

  public void accept(Map<String, Object> result) {
    this.status = ToolCallStatus.ACCEPTED;
    this.result = result;
    this.updatedAt = Instant.now();
  }

  public void reject() {
    this.status = ToolCallStatus.REJECTED;
    this.updatedAt = Instant.now();
  }
}