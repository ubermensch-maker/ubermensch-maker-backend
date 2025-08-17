package com.example.todo.toolcall;

import com.example.todo.message.Message;
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

  @Column(nullable = false)
  private String functionName;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private Map<String, Object> arguments;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private Map<String, Object> result;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ToolCallStatus status;

  @Column
  private String openaiToolCallId;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  private ToolCall(
      User user,
      Message message,
      String functionName,
      Map<String, Object> arguments,
      String openaiToolCallId) {
    this.user = user;
    this.message = message;
    this.functionName = functionName;
    this.arguments = arguments;
    this.openaiToolCallId = openaiToolCallId;
    this.status = ToolCallStatus.PENDING;
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
  }

  public static ToolCall create(
      User user,
      Message message,
      String functionName,
      Map<String, Object> arguments,
      String openaiToolCallId) {
    return new ToolCall(user, message, functionName, arguments, openaiToolCallId);
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