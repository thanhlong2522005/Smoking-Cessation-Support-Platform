package com.example.smoking.platform.repository;

import com.example.smoking.platform.model.QuitPlan;
import com.example.smoking.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuitPlanRepository extends JpaRepository<QuitPlan, Long> {
    List<QuitPlan> findByUser(User user);
    List<QuitPlan> findByUserAndStatus(User user, QuitPlan.Status status);
}