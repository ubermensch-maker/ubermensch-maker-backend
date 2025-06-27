package com.example.todo.goal;

import com.example.todo.goal.enums.GoalStatus;
import com.example.todo.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "goals")
@Getter
@ToString(exclude = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Goal {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private String title;

  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private GoalStatus status;

  @Column(name = "start_at")
  private Instant startAt;

  @Column(name = "end_at")
  private Instant endAt;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public static Goal create(
      User user, String title, String description, Instant startAt, Instant endAt) {
    Goal goal = new Goal();
    goal.user = user;
    goal.title = title;
    goal.description = description;
    goal.status = GoalStatus.NOT_STARTED;
    goal.startAt = startAt;
    goal.endAt = endAt;
    goal.createdAt = Instant.now();
    goal.updatedAt = goal.createdAt;
    return goal;
  }

  public void update(
      String title, String description, GoalStatus status, Instant startAt, Instant endAt) {
    if (title != null) this.title = title;
    if (description != null) this.description = description;
    if (status != null) this.status = status;
    if (startAt != null) this.startAt = startAt;
    if (endAt != null) this.endAt = endAt;
    this.updatedAt = Instant.now();
  }
}
