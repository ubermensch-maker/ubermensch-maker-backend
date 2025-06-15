package com.example.todo.message.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MessageListDto {
    private int total;
    private List<MessageDto> items;
}
