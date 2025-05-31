package com.example.todo.quest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestRepository extends JpaRepository<Quest, Long> {
    List<Quest> findAllByUserId(Long userId);

    List<Quest> findAllByUserIdAndGoalId(Long userId, Long goalId);

    List<Quest> findAllByUserIdAndMilestoneId(Long userId, Long milestoneId);

    void deleteAllByGoalId(Long goalId);

    void deleteAllByMilestoneId(Long milestoneId);
}
