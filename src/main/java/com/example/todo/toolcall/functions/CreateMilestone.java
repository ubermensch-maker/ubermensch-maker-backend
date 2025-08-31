package com.example.todo.toolcall.functions;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.time.Instant;

@JsonClassDescription("Creates a new milestone for a specific goal")
public class CreateMilestone {

  @JsonPropertyDescription("The ID of the goal this milestone belongs to")
  public Long goalId;

  @JsonPropertyDescription("The title of the milestone")
  public String title;

  @JsonPropertyDescription("The description of the milestone")
  public String description;

  @JsonPropertyDescription("The start date of the milestone in ISO 8601 format (e.g., 2024-01-01T00:00:00Z)")
  public Instant startAt;

  @JsonPropertyDescription("The end date of the milestone in ISO 8601 format (e.g., 2024-12-31T23:59:59Z)")
  public Instant endAt;
}