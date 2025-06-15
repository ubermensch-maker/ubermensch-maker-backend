package com.example.todo.conversation;

import com.example.todo.goal.Goal;
import com.example.todo.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Entity
@Table(name = "conversations")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Conversation {
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

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static Conversation create(User user, Goal goal, String title) {
        Conversation conversation = new Conversation();
        conversation.user = user;
        conversation.goal = goal;
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
