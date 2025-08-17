package com.example.todo.toolcall;

import com.example.todo.goal.GoalService;
import com.example.todo.goal.dto.GoalCreateDto;
import com.example.todo.goal.dto.GoalDto;
import com.example.todo.milestone.MilestoneService;
import com.example.todo.milestone.dto.MilestoneCreateDto;
import com.example.todo.milestone.dto.MilestoneDto;
import com.example.todo.quest.QuestService;
import com.example.todo.quest.dto.QuestCreateDto;
import com.example.todo.quest.dto.QuestDto;
import com.example.todo.quest.enums.QuestType;
import com.example.todo.toolcall.dto.ToolCallActionDto;
import com.example.todo.toolcall.enums.ToolCallStatus;
import com.example.todo.user.User;
import com.example.todo.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ToolCallService {
  private final ToolCallRepository toolCallRepository;
  private final UserRepository userRepository;
  private final GoalService goalService;
  private final MilestoneService milestoneService;
  private final QuestService questService;
  private final ObjectMapper objectMapper;

  @Transactional
  public ToolCall executeAction(Long userId, UUID toolCallId, ToolCallActionDto request) {
    User user = userRepository
        .findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    ToolCall toolCall = toolCallRepository
        .findById(toolCallId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tool call not found"));

    if (!userId.equals(toolCall.getUser().getId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }

    if (!ToolCallStatus.PENDING.equals(toolCall.getStatus())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tool call already processed");
    }

    if ("accept".equals(request.getAction())) {
      Map<String, Object> result = executeTool(userId, toolCall);
      toolCall.accept(result);
    } else if ("reject".equals(request.getAction())) {
      toolCall.reject();
    } else {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid action");
    }

    return toolCallRepository.save(toolCall);
  }

  private Map<String, Object> executeTool(Long userId, ToolCall toolCall) {
    Map<String, Object> arguments = toolCall.getArguments();
    String functionName = toolCall.getFunctionName();

    try {
      switch (functionName) {
        case "CreateGoal":
          return executeCreateGoal(userId, arguments);
        case "CreateMilestone":
          return executeCreateMilestone(userId, arguments);
        case "CreateQuest":
          return executeCreateQuest(userId, arguments);
        default:
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown function: " + functionName);
      }
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Function execution failed", e);
    }
  }

  private Map<String, Object> executeCreateGoal(Long userId, Map<String, Object> arguments) {
    GoalCreateDto createDto = objectMapper.convertValue(arguments, GoalCreateDto.class);
    GoalDto goal = goalService.create(userId, createDto);
    
    Map<String, Object> result = new HashMap<>();
    result.put("success", true);
    result.put("goalId", goal.getId());
    result.put("title", goal.getTitle());
    return result;
  }

  private Map<String, Object> executeCreateMilestone(Long userId, Map<String, Object> arguments) {
    MilestoneCreateDto createDto = objectMapper.convertValue(arguments, MilestoneCreateDto.class);
    MilestoneDto milestone = milestoneService.create(userId, createDto);
    
    Map<String, Object> result = new HashMap<>();
    result.put("success", true);
    result.put("milestoneId", milestone.getId());
    result.put("title", milestone.getTitle());
    return result;
  }

  private Map<String, Object> executeCreateQuest(Long userId, Map<String, Object> arguments) {
    Map<String, Object> adjustedArgs = new HashMap<>(arguments);
    if (adjustedArgs.containsKey("type") && adjustedArgs.get("type") instanceof String) {
      String typeStr = (String) adjustedArgs.get("type");
      adjustedArgs.put("type", QuestType.valueOf(typeStr.toUpperCase()));
    }
    
    QuestCreateDto createDto = objectMapper.convertValue(adjustedArgs, QuestCreateDto.class);
    QuestDto quest = questService.create(userId, createDto);
    
    Map<String, Object> result = new HashMap<>();
    result.put("success", true);
    result.put("questId", quest.getId());
    result.put("title", quest.getTitle());
    return result;
  }
}