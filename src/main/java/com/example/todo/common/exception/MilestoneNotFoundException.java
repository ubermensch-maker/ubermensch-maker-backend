package com.example.todo.common.exception;

public class MilestoneNotFoundException extends CustomException {
    public MilestoneNotFoundException() {
        super(ErrorCode.MILESTONE_NOT_FOUND);
    }
}