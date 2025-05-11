package com.example.todo.task;

import com.example.todo.task.dto.TaskCreateDto;
import com.example.todo.task.dto.TaskUpdateDto;
import com.example.todo.task.dto.TaskDto;
import com.example.todo.goal.Goal;
import com.example.todo.user.User;
import com.example.todo.goal.GoalRepository;
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

    @Transactional
    public TaskDto create(TaskCreateDto request) {
        // TODO(jiyoung): replace with login user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Goal goal = null;
        if (request.getGoalId() != null) {
            goal = goalRepository.findById(request.getGoalId())
                    .orElseThrow(() -> new RuntimeException("Goal not found"));
        }

        Task task = Task.create(
                user,
                goal,
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

    public List<TaskDto> list(Long userId) {
        List<Task> tasks = taskRepository.findAllByUserId(userId);
        return tasks.stream().map(TaskDto::from).toList();
    }

    @Transactional
    public TaskDto update(Long taskId, TaskUpdateDto request) {
        // TODO(jiyoung): replace with login user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!user.getId().equals(task.getUser().getId())) {
            throw new RuntimeException("Unauthorized: not the task owner");
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
        // TODO(jiyoung): replace with login user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!user.getId().equals(task.getUser().getId())) {
            throw new RuntimeException("Unauthorized: not the task owner");
        }

        taskRepository.delete(task);
    }
}
