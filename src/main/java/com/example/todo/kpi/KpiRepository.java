package com.example.todo.kpi;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KpiRepository extends JpaRepository<Kpi, Long> {
    List<Kpi> findAllByUserId(Long userId);

    List<Kpi> findAllByUserIdAndGoalId(Long userId, Long goalId);

    void deleteAllByGoalId(Long goalId);
}
