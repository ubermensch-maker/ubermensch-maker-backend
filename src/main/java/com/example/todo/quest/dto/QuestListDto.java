package com.example.todo.quest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class QuestListDto {
    private int total;
    private List<QuestDto> items;
}
