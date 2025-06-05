package com.example.smoking.platform.repository;

import com.example.smoking.platform.model.SmokingLog;
import com.example.smoking.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SmokingLogRepository extends JpaRepository<SmokingLog, Long> {
    List<SmokingLog> findByUser(User user);
}