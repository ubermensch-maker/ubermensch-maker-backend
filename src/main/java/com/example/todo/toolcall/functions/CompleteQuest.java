package com.example.todo.toolcall.functions;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@JsonClassDescription("Completes a quest by changing its status to COMPLETED")
public class CompleteQuest {

  @JsonPropertyDescription("The ID of the quest to complete")
  public Long questId;
}
