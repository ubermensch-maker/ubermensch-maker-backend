package com.example.todo.task;

import com.example.todo.task.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByUserId(Long userId);

    List<Task> findAllByUserIdAndGoalId(Long userId, Long goalId);

    List<Task> findAllByUserIdAndKpiId(Long userId, Long kpiId);

    List<Task> findAllByStatus(TaskStatus status);
}
