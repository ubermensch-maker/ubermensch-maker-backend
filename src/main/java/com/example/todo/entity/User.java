package com.example.todo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Entity
@Table(name = "user")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private String timezone;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static User create(String email, String password, String name, UserRole role, String timezone) {
        User user = new User();
        user.email = email;
        user.password = password;
        user.name = name;
        user.role = role;
        user.timezone = timezone;
        user.createdAt = Instant.now();
        user.updatedAt = user.createdAt;
        return user;
    }

    public void update(String email, String password, String name, String timezone) {
        if (email != null) this.email = email;
        if (password != null) this.password = password;
        if (name != null) this.name = name;
        if (timezone != null) this.timezone = timezone;
        this.updatedAt = Instant.now();
    }
}
