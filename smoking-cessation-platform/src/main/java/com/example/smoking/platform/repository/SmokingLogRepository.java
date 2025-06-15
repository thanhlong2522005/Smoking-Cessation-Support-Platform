package com.example.smoking.platform.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    // Phương thức mới: Tìm nhật ký hút thuốc gần đây nhất của một người dùng
    Optional<SmokingLog> findTopByUserOrderByDateDesc(User user);

    // Phương thức mới: Tìm tất cả nhật ký hút thuốc của người dùng sau một ngày cụ thể
    List<SmokingLog> findByUserAndDateAfter(User user, LocalDateTime date);

    List<SmokingLog> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDateTime startDate, LocalDateTime endDate);

    List<SmokingLog> findByUserOrderByDateDesc(User user);

    List<SmokingLog> findByUserAndDateAfterOrderByDateAsc(User user, LocalDateTime date);

    
}