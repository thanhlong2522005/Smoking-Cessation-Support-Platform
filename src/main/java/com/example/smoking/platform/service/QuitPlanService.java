package com.example.smoking.platform.service;

import com.example.smoking.platform.model.QuitPlan;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.repository.QuitPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QuitPlanService {

    @Autowired
    private QuitPlanRepository quitPlanRepository;

    public QuitPlan save(QuitPlan quitPlan) {
        if (quitPlan.getUser() == null) {
            throw new IllegalArgumentException("User là bắt buộc.");
        }
        if (quitPlan.getReason() == null || quitPlan.getReason().isBlank()) {
            throw new IllegalArgumentException("Lý do cai thuốc là bắt buộc.");
        }
        if (quitPlan.getStartDate() == null) {
            throw new IllegalArgumentException("Ngày bắt đầu là bắt buộc.");
        }
        // CẬP NHẬT: Sử dụng getEndDate() thay vì getTargetDate()
        if (quitPlan.getEndDate() == null) {
            throw new IllegalArgumentException("Ngày kết thúc là bắt buộc."); // Đổi từ "Ngày mục tiêu"
        }
        // CẬP NHẬT: Sử dụng getEndDate() thay vì getTargetDate()
        if (quitPlan.getEndDate().isBefore(quitPlan.getStartDate())) { // Đổi từ getTargetDate()
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu."); // Đổi từ "Ngày mục tiêu"
        }
        if (quitPlan.getPhases() == null || quitPlan.getPhases().isEmpty()) {
            throw new IllegalArgumentException("Kế hoạch cai thuốc phải có ít nhất một giai đoạn.");
        }
        if (quitPlan.getStatus() == null) {
            quitPlan.setStatus(QuitPlan.Status.PLANNING);
        }
        return quitPlanRepository.save(quitPlan);
    }

    public List<QuitPlan> getPlansByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User không được null.");
        }
        return quitPlanRepository.findByUser(user);
    }

    public Optional<QuitPlan> updateStatus(Long planId, QuitPlan.Status status) {
        QuitPlan plan = quitPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Kế hoạch không tồn tại với ID: " + planId));
        plan.setStatus(status);
        return Optional.of(quitPlanRepository.save(plan));
    }

    public QuitPlan getActivePlan(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User không được null.");
        }
        return quitPlanRepository.findByUserAndStatus(user, QuitPlan.Status.IN_PROGRESS)
                .stream()
                .findFirst()
                .orElse(null);
    }

    /**
     * Tìm một QuitPlan theo ID của nó.
     * @param id ID của QuitPlan.
     * @return Một Optional chứa QuitPlan nếu tìm thấy, ngược lại là Optional trống.
     */
    public Optional<QuitPlan> findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID kế hoạch không được null.");
        }
        return quitPlanRepository.findById(id);
    }
    public void delete(Long planId) {
        quitPlanRepository.deleteById(planId);
    }
}