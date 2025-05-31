package com.example.todo.common.exception;

public class GoalNotFoundException extends CustomException {
    public GoalNotFoundException() {
        super(ErrorCode.GOAL_NOT_FOUND);
    }
}