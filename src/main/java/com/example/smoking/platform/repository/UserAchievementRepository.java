package com.example.smoking.platform.repository;

import com.example.smoking.platform.model.Achievement;
import com.example.smoking.platform.model.UserAchievement;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    List<UserAchievement> findByUserOrderByDateAchievedAsc(User user); // Lấy tất cả huy hiệu của một user, đã sắp xếp
    List<UserAchievement> findByAchievement(Achievement achievement);
    Optional<UserAchievement> findByUserAndAchievement(User user, Achievement achievement); // Kiểm tra xem user đã đạt huy hiệu này chưa
    void deleteByAchievement(Achievement achievement);
}