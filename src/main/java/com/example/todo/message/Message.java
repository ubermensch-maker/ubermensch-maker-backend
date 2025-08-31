package com.example.todo.message;

import com.example.todo.conversation.Conversation;
import com.example.todo.message.dto.ContentDto;
import com.example.todo.message.enums.MessageRole;
import com.example.todo.message.enums.Model;
import com.example.todo.message.enums.ModelConverter;
import com.example.todo.toolcall.ToolCall;
import com.example.todo.usage.TokenUsage;
import com.example.todo.user.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "chat_messages")
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE chat_messages SET deleted_at = NOW() WHERE id = ?")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {
  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(columnDefinition = "uuid default uuid_generate_v7()")
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "conversation_id", nullable = false)
  private Conversation conversation;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_message_id")
  private Message parentMessage;

  @Column(name = "message_index", nullable = false)
  private Integer index = 0;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MessageRole role;

  @Column(nullable = false)
  @Convert(converter = ModelConverter.class)
  private Model model;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private List<ContentDto> content;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  @OneToMany(mappedBy = "message", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<ToolCall> toolCalls;

  @OneToMany(mappedBy = "message", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<TokenUsage> tokenUsages;

  public static Message create(
      @Nullable User user,
      Conversation conversation,
      @Nullable Message parentMessage,
      Integer index,
      Model model,
      MessageRole role,
      List<ContentDto> content) {
    Message message = new Message();
    message.user = user;
    message.conversation = conversation;
    message.parentMessage = parentMessage;
    message.index = index;
    message.model = model;
    message.role = role;
    message.content = content;
    message.createdAt = Instant.now();
    message.updatedAt = message.createdAt;
    return message;
  }
}
