package com.example.todo.quest;

import com.example.todo.common.exception.*;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestService {
    private final QuestRepository questRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final MilestoneRepository milestoneRepository;

    @Transactional
    public QuestDto create(Long userId, QuestCreateDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Goal goal = null;
        if (request.getGoalId() != null) {
            goal = goalRepository.findById(request.getGoalId())
                    .orElseThrow(GoalNotFoundException::new);
        }

        Milestone milestone = null;
        if (request.getMilestoneId() != null) {
            milestone = milestoneRepository.findById(request.getMilestoneId())
                    .orElseThrow(MilestoneNotFoundException::new);
        }

        Quest quest = Quest.create(
                user,
                goal,
                milestone,
                request.getTitle(),
                request.getDescription(),
                request.getStartAt(),
                request.getEndAt()
        );

        return QuestDto.from(questRepository.save(quest));
    }

    public QuestDto read(Long questId) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(QuestNotFoundException::new);

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

        return new QuestListDto(
                quests.size(),
                quests.stream().map(QuestDto::from).toList()
        );
    }

    @Transactional
    public QuestDto update(Long userId, Long questId, QuestUpdateDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Quest quest = questRepository.findById(questId)
                .orElseThrow(QuestNotFoundException::new);

        if (!user.getId().equals(quest.getUser().getId())) {
            throw new ForbiddenException();
        }

        quest.update(
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getStartAt(),
                request.getEndAt()
        );

        return QuestDto.from(quest);
    }

    @Transactional
    public void delete(Long userId, Long questId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Quest quest = questRepository.findById(questId)
                .orElseThrow(QuestNotFoundException::new);

        if (!user.getId().equals(quest.getUser().getId())) {
            throw new ForbiddenException();
        }

        questRepository.delete(quest);
    }
}
