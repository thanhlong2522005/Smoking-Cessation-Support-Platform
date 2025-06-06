package com.example.smoking.platform.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.smoking.platform.model.SmokingLog;
import com.example.smoking.platform.model.User;

public interface SmokingLogRepository extends JpaRepository<SmokingLog, Long> {

    // Lấy danh sách logs theo user
    List<SmokingLog> findByUser(User user);

    // Lấy logs theo user và khoảng thời gian
    List<SmokingLog> findByUserAndDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate);

    // Lấy logs theo user và tần suất (frequency)
    List<SmokingLog> findByUserAndFrequency(User user, SmokingLog.Frequency frequency);

    // Đếm tổng số điếu hút theo user
    @Query("SELECT SUM(sl.cigarettesSmoked) FROM SmokingLog sl WHERE sl.user = :user")
    Integer countTotalCigarettesByUser(@Param("user") User user);

    // Lấy ngày cuối cùng có hút thuốc theo user
    @Query("SELECT MAX(sl.date) FROM SmokingLog sl WHERE sl.user = :user AND sl.cigarettesSmoked > 0")
    LocalDateTime findLastSmokingDateByUser(@Param("user") User user);
}