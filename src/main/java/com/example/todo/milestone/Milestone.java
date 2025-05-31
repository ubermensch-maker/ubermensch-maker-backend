package com.example.todo.milestone;

import com.example.todo.goal.Goal;
import com.example.todo.milestone.enums.MilestoneStatus;
import com.example.todo.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Entity
@Table(name = "milestones")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Milestone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MilestoneStatus status;

    @Column(name = "start_at")
    private Instant startAt;

    @Column(name = "end_at")
    private Instant endAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static Milestone create(User user, Goal goal, String title, String description, Instant startAt, Instant endAt) {
        Milestone milestone = new Milestone();
        milestone.user = user;
        milestone.goal = goal;
        milestone.title = title;
        milestone.description = description;
        milestone.status = MilestoneStatus.NOT_STARTED;
        milestone.startAt = startAt;
        milestone.endAt = endAt;
        milestone.createdAt = Instant.now();
        milestone.updatedAt = milestone.createdAt;
        return milestone;
    }

    public void update(String title, String description, MilestoneStatus status, Instant startAt, Instant endAt) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (status != null) this.status = status;
        if (startAt != null) this.startAt = startAt;
        if (endAt != null) this.endAt = endAt;
        this.updatedAt = Instant.now();
    }
}
