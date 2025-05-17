package com.example.todo.task;

import com.example.todo.goal.Goal;
import com.example.todo.goal.GoalRepository;
import com.example.todo.kpi.Kpi;
import com.example.todo.kpi.KpiRepository;
import com.example.todo.task.dto.TaskCreateDto;
import com.example.todo.task.dto.TaskDto;
import com.example.todo.task.dto.TaskUpdateDto;
import com.example.todo.user.User;
import com.example.todo.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final KpiRepository kpiRepository;

    @Transactional
    public TaskDto create(TaskCreateDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Goal goal = null;
        if (request.getGoalId() != null) {
            goal = goalRepository.findById(request.getGoalId())
                    .orElseThrow(() -> new RuntimeException("Goal not found"));
        }

        Kpi kpi = null;
        if (request.getKpiId() != null) {
            kpi = kpiRepository.findById(request.getKpiId())
                    .orElseThrow(() -> new RuntimeException("Kpi not found"));
        }

        Task task = Task.create(
                user,
                goal,
                kpi,
                request.getTitle(),
                request.getDescription(),
                request.getStartAt(),
                request.getEndAt()
        );

        return TaskDto.from(taskRepository.save(task));
    }

    public TaskDto read(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return TaskDto.from(task);
    }

    public List<TaskDto> list(Long userId, Long goalId, Long kpiId) {
        List<Task> tasks;

        if (kpiId != null) {
            tasks = taskRepository.findAllByUserIdAndKpiId(userId, kpiId);
        } else if (goalId != null) {
            tasks = taskRepository.findAllByUserIdAndGoalId(userId, goalId);
        } else {
            tasks = taskRepository.findAllByUserId(userId);
        }

        return tasks.stream().map(TaskDto::from).toList();
    }

    @Transactional
    public TaskDto update(Long taskId, TaskUpdateDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!user.getId().equals(task.getUser().getId())) {
            throw new RuntimeException("Unauthorized update");
        }

        task.update(
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getStartAt(),
                request.getEndAt()
        );

        return TaskDto.from(task);
    }

    @Transactional
    public void delete(Long taskId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!user.getId().equals(task.getUser().getId())) {
            throw new RuntimeException("Unauthorized delete");
        }

        taskRepository.delete(task);
    }
}
