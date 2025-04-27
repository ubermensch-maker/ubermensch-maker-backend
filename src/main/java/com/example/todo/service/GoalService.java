package com.example.todo.service;

import com.example.todo.dto.request.GoalCreateRequest;
import com.example.todo.dto.request.GoalUpdateRequest;
import com.example.todo.dto.response.GoalResponse;
import com.example.todo.entity.Goal;
import com.example.todo.entity.User;
import com.example.todo.repository.GoalRepository;
import com.example.todo.repository.UserRepository;
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
    public GoalResponse create(GoalCreateRequest request) {
        // TODO(jiyoung): replace with login user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Goal goal = Goal.create(
                user,
                request.getTitle(),
                request.getDescription(),
                request.getStartAt(),
                request.getEndAt()
        );

        return GoalResponse.from(goalRepository.save(goal));
    }

    public GoalResponse read(Long goalId) {
        Goal goal = goalRepository.findById(goalId).orElseThrow();
        return GoalResponse.from(goal);
    }

    public List<GoalResponse> list(Long userId) {
        List<Goal> goals = goalRepository.findAllByUserId(userId);
        return goals.stream().map(GoalResponse::from).toList();
    }

    @Transactional
    public GoalResponse update(Long goalId, GoalUpdateRequest request) {
        // TODO(jiyoung): replace with login user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        if (!user.getId().equals(goal.getUser().getId())) {
            throw new RuntimeException("Unauthorized: not the goal owner");
        }

        goal.update(
                request.getTitle(),
                request.getDescription(),
                request.getStartAt(),
                request.getEndAt()
        );

        return GoalResponse.from(goal);
    }

    @Transactional
    public void delete(Long goalId, Long userId) {
        // TODO(jiyoung): replace with login user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        if (!user.getId().equals(goal.getUser().getId())) {
            throw new RuntimeException("Unauthorized: not the goal owner");
        }

        goalRepository.delete(goal);
    }
}
