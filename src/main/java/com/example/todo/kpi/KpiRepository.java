package com.example.todo.kpi;

import com.example.todo.kpi.enums.KpiStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KpiRepository extends JpaRepository<Kpi, Long> {
    List<Kpi> findAllByUserId(Long userId);

    List<Kpi> findAllByGoalId(Long goalId);

    List<Kpi> findAllByUserIdAndGoalId(Long userId, Long goalId);

    List<Kpi> findAllByStatus(KpiStatus status);
}
