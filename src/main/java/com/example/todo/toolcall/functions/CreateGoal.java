package com.example.todo.toolcall.functions;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.time.Instant;

@JsonClassDescription("Creates a new goal for the user")
public class CreateGoal {

  @JsonPropertyDescription("The title of the goal")
  public String title;

  @JsonPropertyDescription("The description of the goal")
  public String description;

  @JsonPropertyDescription(
      "The start date of the goal in ISO 8601 format (e.g., 2024-01-01T00:00:00Z)")
  public Instant startAt;

  @JsonPropertyDescription(
      "The end date of the goal in ISO 8601 format (e.g., 2024-12-31T23:59:59Z)")
  public Instant endAt;
}
