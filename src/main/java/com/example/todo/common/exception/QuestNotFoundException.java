package com.example.todo.common.exception;

public class QuestNotFoundException extends CustomException {
    public QuestNotFoundException() {
        super(ErrorCode.QUEST_NOT_FOUND);
    }
}
