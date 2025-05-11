package com.example.todo.kpi;

import com.example.todo.kpi.dto.KpiCreateDto;
import com.example.todo.kpi.dto.KpiDto;
import com.example.todo.kpi.dto.KpiUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class KpiController {
    private final KpiService kpiService;

    @PostMapping("/kpis")
    public KpiDto create(@RequestBody KpiCreateDto request) {
        return kpiService.create(request);
    }

    @GetMapping("/kpis/{kpiId}")
    public KpiDto read(@PathVariable Long kpiId) {
        return kpiService.read(kpiId);
    }

    @GetMapping("/kpis")
    public List<KpiDto> list(@RequestParam Long userId, @RequestParam(required = false) Long goalId) {
        return kpiService.list(userId, goalId);
    }

    @PutMapping("/kpis/{kpiId}")
    public KpiDto update(@PathVariable Long kpiId, @RequestBody KpiUpdateDto request) {
        return kpiService.update(kpiId, request);
    }

    @DeleteMapping("/kpis/{kpiId}")
    public void delete(@PathVariable Long kpiId, @RequestParam Long userId) {
        kpiService.delete(kpiId, userId);
    }
}
