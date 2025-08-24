package com.example.todo.milestone;

import com.example.todo.goal.Goal;
import com.example.todo.goal.GoalRepository;
import com.example.todo.milestone.dto.MilestoneCreateDto;
import com.example.todo.milestone.dto.MilestoneDto;
import com.example.todo.milestone.dto.MilestoneListDto;
import com.example.todo.milestone.dto.MilestoneUpdateDto;
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
public class MilestoneService {
  private final MilestoneRepository milestoneRepository;
  private final UserRepository userRepository;
  private final GoalRepository goalRepository;
  private final QuestRepository questRepository;

  @Transactional
  public MilestoneDto create(Long userId, MilestoneCreateDto request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    Goal goal =
        goalRepository
            .findById(request.getGoalId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found"));

    Milestone milestone =
        Milestone.create(
            user,
            goal,
            request.getTitle(),
            request.getDescription(),
            request.getStartAt(),
            request.getEndAt());

    return MilestoneDto.from(milestoneRepository.save(milestone));
  }

  public MilestoneDto get(Long milestoneId) {
    Milestone milestone =
        milestoneRepository
            .findById(milestoneId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Milestone not found"));

    return MilestoneDto.from(milestone);
  }

  public MilestoneListDto list(Long userId, Long goalId) {
    List<Milestone> milestones;

    if (goalId != null) {
      milestones = milestoneRepository.findAllByUserIdAndGoalId(userId, goalId);
    } else {
      milestones = milestoneRepository.findAllByUserId(userId);
    }

    return new MilestoneListDto(
        milestones.size(), milestones.stream().map(MilestoneDto::from).toList());
  }

  @Transactional
  public MilestoneDto update(Long userId, Long milestoneId, MilestoneUpdateDto request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    Milestone milestone =
        milestoneRepository
            .findById(milestoneId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Milestone not found"));

    if (!user.getId().equals(milestone.getUser().getId())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "You do not have permission to update this milestone");
    }

    milestone.update(
        request.getTitle(),
        request.getDescription(),
        request.getStatus(),
        request.getStartAt(),
        request.getEndAt());

    return MilestoneDto.from(milestone);
  }

  @Transactional
  public void delete(Long userId, Long milestoneId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    Milestone milestone =
        milestoneRepository
            .findById(milestoneId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Milestone not found"));

    if (!user.getId().equals(milestone.getUser().getId())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "You do not have permission to delete this milestone");
    }

    questRepository.deleteAllByMilestoneId(milestoneId);
    milestoneRepository.delete(milestone);
  }
}
