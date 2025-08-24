package com.example.todo.toolcall.functions;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.time.Instant;

@JsonClassDescription("Creates a new quest (task) for a specific goal or milestone")
public class CreateQuest {

  @JsonPropertyDescription("The ID of the goal this quest belongs to")
  public Long goalId;

  @JsonPropertyDescription("The ID of the milestone this quest belongs to (optional)")
  public Long milestoneId;

  @JsonPropertyDescription("The title of the quest")
  public String title;

  @JsonPropertyDescription("The description of the quest")
  public String description;

  @JsonPropertyDescription("The type of the quest (DAILY, WEEKLY, MONTHLY, ONCE)")
  public String type;

  @JsonPropertyDescription("The start date of the quest in ISO 8601 format (e.g., 2024-01-01T00:00:00Z)")
  public Instant startAt;

  @JsonPropertyDescription("The end date of the quest in ISO 8601 format (e.g., 2024-12-31T23:59:59Z)")
  public Instant endAt;
}