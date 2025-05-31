package com.example.todo.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GoalListDto {
    private int total;
    private List<GoalDto> items;
}
