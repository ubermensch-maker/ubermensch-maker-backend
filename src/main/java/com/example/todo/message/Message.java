package com.example.todo.message;

import com.example.todo.conversation.Conversation;
import com.example.todo.message.dto.ContentDto;
import com.example.todo.message.enums.MessageRole;
import com.example.todo.user.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "messages")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Column(nullable = false)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageRole role;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<ContentDto> content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static Message create(@Nullable User user, Conversation conversation, String model, MessageRole role, List<ContentDto> content) {
        Message message = new Message();
        message.user = user;
        message.conversation = conversation;
        message.model = model;
        message.role = role;
        message.content = content;
        message.createdAt = Instant.now();
        message.updatedAt = message.createdAt;
        return message;
    }
}
