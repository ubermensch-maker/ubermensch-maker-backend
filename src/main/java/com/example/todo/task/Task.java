package com.example.todo.task;

import com.example.todo.goal.Goal;
import com.example.todo.kpi.Kpi;
import com.example.todo.task.enums.TaskStatus;
import com.example.todo.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Entity
@Table(name = "tasks")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task {
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
    @JoinColumn(name = "kpi_id")
    private Kpi kpi;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    @Column(name = "start_at")
    private Instant startAt;

    @Column(name = "end_at")
    private Instant endAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static Task create(User user, Goal goal, Kpi kpi, String title, String description, Instant startAt, Instant endAt) {
        Task task = new Task();
        task.user = user;
        task.goal = goal;
        task.kpi = kpi;
        task.title = title;
        task.description = description;
        task.status = TaskStatus.PENDING;
        task.startAt = startAt;
        task.endAt = endAt;
        task.createdAt = Instant.now();
        task.updatedAt = task.createdAt;
        return task;
    }

    public void update(String title, String description, TaskStatus status, Instant startAt, Instant endAt) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (status != null) this.status = status;
        if (startAt != null) this.startAt = startAt;
        if (endAt != null) this.endAt = endAt;
        this.updatedAt = Instant.now();
    }
}
