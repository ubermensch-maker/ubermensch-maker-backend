package com.example.todo.service;

import com.example.todo.dto.request.TaskCreateRequest;
import com.example.todo.dto.request.TaskUpdateRequest;
import com.example.todo.dto.response.TaskResponse;
import com.example.todo.entity.Goal;
import com.example.todo.entity.Task;
import com.example.todo.entity.User;
import com.example.todo.repository.GoalRepository;
import com.example.todo.repository.TaskRepository;
import com.example.todo.repository.UserRepository;
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
    public TaskResponse create(TaskCreateRequest request) {
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

        return TaskResponse.from(taskRepository.save(task));
    }

    public TaskResponse read(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return TaskResponse.from(task);
    }

    public List<TaskResponse> list(Long userId) {
        List<Task> tasks = taskRepository.findAllByUserId(userId);
        return tasks.stream().map(TaskResponse::from).toList();
    }

    @Transactional
    public TaskResponse update(Long taskId, TaskUpdateRequest request) {
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

        return TaskResponse.from(task);
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
