package com.example.todo.message.dto;

import com.example.todo.message.enums.MessageRole;
import lombok.Getter;

import java.util.List;

@Getter
public class MessageCreateDto {
    private Long conversationId;
    private String model;
    private MessageRole role;
    private List<ContentDto> content;
}
