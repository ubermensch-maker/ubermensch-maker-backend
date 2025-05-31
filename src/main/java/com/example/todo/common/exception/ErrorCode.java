package com.example.todo.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Authentication is required"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Access is denied"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "Goal not found"),
    MILESTONE_NOT_FOUND(HttpStatus.NOT_FOUND, "Milestone not found"),
    QUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "Quest not found");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}