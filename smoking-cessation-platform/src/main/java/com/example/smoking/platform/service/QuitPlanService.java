package com.example.smoking.platform.service;

import com.example.smoking.platform.model.QuitPlan;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.repository.QuitPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuitPlanService {

    @Autowired
    private QuitPlanRepository quitPlanRepository;

    public QuitPlan save(QuitPlan quitPlan) {
        if (quitPlan.getUser() == null) {
            throw new IllegalArgumentException("User là bắt buộc");
        }
        if (quitPlan.getReason() == null || quitPlan.getReason().isBlank()) {
            throw new IllegalArgumentException("Lý do cai thuốc là bắt buộc");
        }
        if (quitPlan.getStartDate() == null) {
            throw new IllegalArgumentException("Ngày bắt đầu là bắt buộc");
        }
        if (quitPlan.getPhases() == null || quitPlan.getPhases().isEmpty()) {
            throw new IllegalArgumentException("Cần ít nhất một giai đoạn");
        }
        if (quitPlan.getStatus() == null) {
            quitPlan.setStatus(QuitPlan.Status.PLANNING);
        }
        return quitPlanRepository.save(quitPlan);
    }

    public List<QuitPlan> getPlansByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User không được null");
        }
        return quitPlanRepository.findByUser(user);
    }

    public QuitPlan updateStatus(Long planId, QuitPlan.Status status) {
        QuitPlan plan = quitPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Kế hoạch không tồn tại"));
        plan.setStatus(status);
        return quitPlanRepository.save(plan);
    }

    public QuitPlan getActivePlan(User user) {
        return quitPlanRepository.findByUserAndStatus(user, QuitPlan.Status.IN_PROGRESS)
                .stream()
                .findFirst()
                .orElse(null);
    }
}