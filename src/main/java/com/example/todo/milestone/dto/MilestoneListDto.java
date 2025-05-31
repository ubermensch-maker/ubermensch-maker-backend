package com.example.todo.milestone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MilestoneListDto {
    private int total;
    private List<MilestoneDto> items;
}
