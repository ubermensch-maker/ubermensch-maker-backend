package com.example.todo.common.exception;

public class ForbiddenException extends CustomException {
    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN);
    }
}
