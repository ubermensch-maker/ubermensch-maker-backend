package com.example.todo.milestone;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
  List<Milestone> findAllByUserId(Long userId);

  List<Milestone> findAllByUserIdAndGoalId(Long userId, Long goalId);
}
