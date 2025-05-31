package com.example.todo.milestone;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
    List<Milestone> findAllByUserId(Long userId);

    List<Milestone> findAllByUserIdAndGoalId(Long userId, Long goalId);

    void deleteAllByGoalId(Long goalId);
}
