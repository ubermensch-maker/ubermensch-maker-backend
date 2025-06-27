package com.example.todo.goal;

import com.example.todo.goal.dto.GoalCreateDto;
import com.example.todo.goal.dto.GoalDto;
import com.example.todo.goal.dto.GoalListDto;
import com.example.todo.goal.dto.GoalUpdateDto;
import com.example.todo.milestone.MilestoneRepository;
import com.example.todo.quest.QuestRepository;
import com.example.todo.user.User;
import com.example.todo.user.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class GoalService {
  private final GoalRepository goalRepository;
  private final UserRepository userRepository;
  private final MilestoneRepository milestoneRepository;
  private final QuestRepository questRepository;

  @Transactional
  public GoalDto create(Long userId, GoalCreateDto request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    Goal goal =
        Goal.create(
            user,
            request.getTitle(),
            request.getDescription(),
            request.getStartAt(),
            request.getEndAt());

    return GoalDto.from(goalRepository.save(goal));
  }

  public GoalDto read(Long goalId) {
    Goal goal =
        goalRepository
            .findById(goalId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found"));

    return GoalDto.from(goal);
  }

  public GoalListDto list(Long userId) {
    List<Goal> goals = goalRepository.findAllByUserId(userId);

    return new GoalListDto(goals.size(), goals.stream().map(GoalDto::from).toList());
  }

  @Transactional
  public GoalDto update(Long userId, Long goalId, GoalUpdateDto request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    Goal goal =
        goalRepository
            .findById(goalId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found"));

    if (!user.getId().equals(goal.getUser().getId())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "You do not have permission to update this goal");
    }

    goal.update(
        request.getTitle(),
        request.getDescription(),
        request.getStatus(),
        request.getStartAt(),
        request.getEndAt());

    return GoalDto.from(goal);
  }

  @Transactional
  public void delete(Long userId, Long goalId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    Goal goal =
        goalRepository
            .findById(goalId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found"));

    if (!user.getId().equals(goal.getUser().getId())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "You do not have permission to delete this goal");
    }

    questRepository.deleteAllByGoalId(goalId);
    milestoneRepository.deleteAllByGoalId(goalId);
    goalRepository.delete(goal);
  }
}
