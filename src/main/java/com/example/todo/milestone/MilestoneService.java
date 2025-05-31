package com.example.todo.milestone;

import com.example.todo.common.exception.ForbiddenException;
import com.example.todo.common.exception.GoalNotFoundException;
import com.example.todo.common.exception.MilestoneNotFoundException;
import com.example.todo.common.exception.UserNotFoundException;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MilestoneService {
    private final MilestoneRepository milestoneRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final QuestRepository questRepository;

    @Transactional
    public MilestoneDto create(Long userId, MilestoneCreateDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Goal goal = goalRepository.findById(request.getGoalId())
                .orElseThrow(GoalNotFoundException::new);

        Milestone milestone = Milestone.create(
                user,
                goal,
                request.getTitle(),
                request.getDescription(),
                request.getStartAt(),
                request.getEndAt()
        );

        return MilestoneDto.from(milestoneRepository.save(milestone));
    }

    public MilestoneDto read(Long milestoneId) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(MilestoneNotFoundException::new);

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
                milestones.size(),
                milestones.stream().map(MilestoneDto::from).toList()
        );
    }

    @Transactional
    public MilestoneDto update(Long userId, Long milestoneId, MilestoneUpdateDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(MilestoneNotFoundException::new);

        if (!user.getId().equals(milestone.getUser().getId())) {
            throw new ForbiddenException();
        }

        milestone.update(
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getStartAt(),
                request.getEndAt()
        );

        return MilestoneDto.from(milestone);
    }

    @Transactional
    public void delete(Long userId, Long milestoneId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(MilestoneNotFoundException::new);

        if (!user.getId().equals(milestone.getUser().getId())) {
            throw new ForbiddenException();
        }

        questRepository.deleteAllByMilestoneId(milestoneId);
        milestoneRepository.delete(milestone);
    }
}
