package com.example.todo.quest;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestRepository extends JpaRepository<Quest, Long> {
  List<Quest> findAllByUserId(Long userId);

  List<Quest> findAllByUserIdAndGoalId(Long userId, Long goalId);

  List<Quest> findAllByUserIdAndMilestoneId(Long userId, Long milestoneId);
}
