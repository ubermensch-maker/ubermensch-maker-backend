package com.example.todo.conversation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ConversationListDto {
    private int total;
    private List<ConversationDto> items;
}
