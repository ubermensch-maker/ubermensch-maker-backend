package com.example.todo.goal;

import com.example.todo.goal.dto.GoalCreateDto;
import com.example.todo.goal.dto.GoalDto;
import com.example.todo.goal.dto.GoalUpdateDto;
import com.example.todo.user.User;
import com.example.todo.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    @Transactional
    public GoalDto create(GoalCreateDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Goal goal = Goal.create(
                user,
                request.getTitle(),
                request.getDescription(),
                request.getStartAt(),
                request.getEndAt()
        );

        return GoalDto.from(goalRepository.save(goal));
    }

    public GoalDto read(Long goalId) {
        Goal goal = goalRepository.findById(goalId).orElseThrow();
        return GoalDto.from(goal);
    }

    public List<GoalDto> list(Long userId) {
        List<Goal> goals = goalRepository.findAllByUserId(userId);
        return goals.stream().map(GoalDto::from).toList();
    }

    @Transactional
    public GoalDto update(Long goalId, GoalUpdateDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        if (!user.getId().equals(goal.getUser().getId())) {
            throw new RuntimeException("Unauthorized update");
        }

        goal.update(
                request.getTitle(),
                request.getDescription(),
                request.getStartAt(),
                request.getEndAt()
        );

        return GoalDto.from(goal);
    }

    @Transactional
    public void delete(Long goalId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        if (!user.getId().equals(goal.getUser().getId())) {
            throw new RuntimeException("Unauthorized delete");
        }

        // TODO(jiyoung): delete related kpis and tasks
        goalRepository.delete(goal);
    }
}
