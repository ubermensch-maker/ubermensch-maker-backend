package com.example.todo.conversation;

import com.example.todo.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "chat_conversations")
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE chat_conversations SET deleted_at = NOW() WHERE id = ?")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Conversation {
  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(columnDefinition = "uuid default uuid_generate_v7()")
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private String title;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  public static Conversation create(User user, String title) {
    Conversation conversation = new Conversation();
    conversation.user = user;
    conversation.title = title;
    conversation.createdAt = Instant.now();
    conversation.updatedAt = conversation.createdAt;
    return conversation;
  }

  public void update(String title) {
    if (title != null) this.title = title;
    this.updatedAt = Instant.now();
  }
}
