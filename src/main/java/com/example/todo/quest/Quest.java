package com.example.todo.quest;

import com.example.todo.goal.Goal;
import com.example.todo.milestone.Milestone;
import com.example.todo.quest.enums.QuestStatus;
import com.example.todo.quest.enums.QuestType;
import com.example.todo.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "quests")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quest {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "goal_id")
  private Goal goal;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "milestone_id")
  private Milestone milestone;

  @Column(nullable = false)
  private String title;

  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private QuestType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private QuestStatus status;

  @Column(name = "start_at")
  private Instant startAt;

  @Column(name = "end_at")
  private Instant endAt;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public static Quest create(
      User user,
      Goal goal,
      Milestone milestone,
      String title,
      String description,
      QuestType type,
      Instant startAt,
      Instant endAt) {
    Quest quest = new Quest();
    quest.user = user;
    quest.goal = goal;
    quest.milestone = milestone;
    quest.title = title;
    quest.description = description;
    quest.type = type;
    quest.status = QuestStatus.NOT_STARTED;
    quest.startAt = startAt;
    quest.endAt = endAt;
    quest.createdAt = Instant.now();
    quest.updatedAt = quest.createdAt;
    return quest;
  }

  public void update(
      String title,
      String description,
      QuestType type,
      QuestStatus status,
      Instant startAt,
      Instant endAt) {
    if (title != null) this.title = title;
    if (description != null) this.description = description;
    if (type != null) this.type = type;
    if (status != null) this.status = status;
    if (startAt != null) this.startAt = startAt;
    if (endAt != null) this.endAt = endAt;
    this.updatedAt = Instant.now();
  }
}
