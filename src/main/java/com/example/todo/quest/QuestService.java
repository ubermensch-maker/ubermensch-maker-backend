package com.example.todo.quest;

import com.example.todo.goal.Goal;
import com.example.todo.goal.GoalRepository;
import com.example.todo.milestone.Milestone;
import com.example.todo.milestone.MilestoneRepository;
import com.example.todo.quest.dto.QuestCreateDto;
import com.example.todo.quest.dto.QuestDto;
import com.example.todo.quest.dto.QuestListDto;
import com.example.todo.quest.dto.QuestUpdateDto;
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
public class QuestService {
  private final QuestRepository questRepository;
  private final UserRepository userRepository;
  private final GoalRepository goalRepository;
  private final MilestoneRepository milestoneRepository;

  @Transactional
  public QuestDto create(Long userId, QuestCreateDto request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    Goal goal = null;
    if (request.getGoalId() != null) {
      goal =
          goalRepository
              .findById(request.getGoalId())
              .orElseThrow(
                  () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found"));
    }

    Milestone milestone = null;
    if (request.getMilestoneId() != null) {
      milestone =
          milestoneRepository
              .findById(request.getMilestoneId())
              .orElseThrow(
                  () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Milestone not found"));
    }

    Quest quest =
        Quest.create(
            user,
            goal,
            milestone,
            request.getTitle(),
            request.getDescription(),
            request.getType(),
            request.getStartAt(),
            request.getEndAt());

    return QuestDto.from(questRepository.save(quest));
  }

  public QuestDto read(Long questId) {
    Quest quest =
        questRepository
            .findById(questId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quest not found"));

    return QuestDto.from(quest);
  }

  public QuestListDto list(Long userId, Long goalId, Long milestoneId) {
    List<Quest> quests;

    if (milestoneId != null) {
      quests = questRepository.findAllByUserIdAndMilestoneId(userId, milestoneId);
    } else if (goalId != null) {
      quests = questRepository.findAllByUserIdAndGoalId(userId, goalId);
    } else {
      quests = questRepository.findAllByUserId(userId);
    }

    return new QuestListDto(quests.size(), quests.stream().map(QuestDto::from).toList());
  }

  @Transactional
  public QuestDto update(Long userId, Long questId, QuestUpdateDto request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    Quest quest =
        questRepository
            .findById(questId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quest not found"));

    if (!user.getId().equals(quest.getUser().getId())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "You do not have permission to update this quest");
    }

    quest.update(
        request.getTitle(),
        request.getDescription(),
        request.getType(),
        request.getStatus(),
        request.getStartAt(),
        request.getEndAt());

    return QuestDto.from(quest);
  }

  @Transactional
  public void delete(Long userId, Long questId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    Quest quest =
        questRepository
            .findById(questId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quest not found"));

    if (!user.getId().equals(quest.getUser().getId())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "You do not have permission to delete this quest");
    }

    questRepository.delete(quest);
  }
}
