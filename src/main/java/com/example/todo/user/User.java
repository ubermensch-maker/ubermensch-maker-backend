package com.example.todo.user;

import com.example.todo.conversation.Conversation;
import com.example.todo.goal.Goal;
import com.example.todo.milestone.Milestone;
import com.example.todo.quest.Quest;
import com.example.todo.toolcall.ToolCall;
import com.example.todo.usage.TokenUsage;
import com.example.todo.user.enums.OAuthProvider;
import com.example.todo.user.enums.UserRole;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "users")
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
@Getter
@ToString(exclude = {"goals", "milestones", "quests", "conversations", "toolCalls", "tokenUsages"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String name;

  @Column(name = "picture_url")
  private String picture;

  @Enumerated(EnumType.STRING)
  @Column(name = "oauth_provider", nullable = false)
  private OAuthProvider provider;

  @Column(name = "oauth_provider_id", nullable = false)
  private String providerId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserRole role;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<Goal> goals;

  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<Milestone> milestones;

  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<Quest> quests;

  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<Conversation> conversations;

  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<ToolCall> toolCalls;

  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<TokenUsage> tokenUsages;

  public static User createFromOAuth(
      String email, String name, String picture, OAuthProvider provider, String providerId) {
    User user = new User();
    user.email = email;
    user.name = name;
    user.picture = picture;
    user.provider = provider;
    user.providerId = providerId;
    user.role = UserRole.USER;
    user.createdAt = Instant.now();
    user.updatedAt = user.createdAt;
    return user;
  }

  public void updateProfile(String name) {
    if (name != null) this.name = name;
    this.updatedAt = Instant.now();
  }
}
