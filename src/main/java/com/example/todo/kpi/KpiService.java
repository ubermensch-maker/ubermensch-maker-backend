package com.example.todo.kpi;

import com.example.todo.goal.Goal;
import com.example.todo.goal.GoalRepository;
import com.example.todo.kpi.dto.KpiCreateDto;
import com.example.todo.kpi.dto.KpiDto;
import com.example.todo.kpi.dto.KpiUpdateDto;
import com.example.todo.task.TaskRepository;
import com.example.todo.user.User;
import com.example.todo.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KpiService {
    private final KpiRepository kpiRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public KpiDto create(KpiCreateDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Goal goal = goalRepository.findById(request.getGoalId())
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        Kpi kpi = Kpi.create(
                user,
                goal,
                request.getTitle(),
                request.getDescription(),
                request.getStartAt(),
                request.getEndAt()
        );

        return KpiDto.from(kpiRepository.save(kpi));
    }

    public KpiDto read(Long kpiId) {
        Kpi kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> new RuntimeException("Kpi not found"));
        return KpiDto.from(kpi);
    }

    public List<KpiDto> list(Long userId, Long goalId) {
        List<Kpi> kpis;

        if (goalId != null) {
            kpis = kpiRepository.findAllByUserIdAndGoalId(userId, goalId);
        } else {
            kpis = kpiRepository.findAllByUserId(userId);
        }

        return kpis.stream().map(KpiDto::from).toList();
    }

    @Transactional
    public KpiDto update(Long kpiId, KpiUpdateDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Kpi kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> new RuntimeException("Kpi not found"));

        if (!user.getId().equals(kpi.getUser().getId())) {
            throw new RuntimeException("Unauthorized update");
        }

        kpi.update(
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getStartAt(),
                request.getEndAt()
        );

        return KpiDto.from(kpi);
    }

    @Transactional
    public void delete(Long kpiId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Kpi kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> new RuntimeException("Kpi not found"));

        if (!user.getId().equals(kpi.getUser().getId())) {
            throw new RuntimeException("Unauthorized delete");
        }

        taskRepository.deleteAllByKpiId(kpiId);

        kpiRepository.delete(kpi);
    }
}
