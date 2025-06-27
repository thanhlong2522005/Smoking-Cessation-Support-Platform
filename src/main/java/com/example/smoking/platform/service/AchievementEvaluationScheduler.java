package com.example.smoking.platform.service;

import com.example.smoking.platform.model.User;
import com.example.smoking.platform.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AchievementEvaluationScheduler {

    @Autowired
    private UserService userService; // Dùng để lấy danh sách người dùng

    @Autowired
    private AchievementService achievementService; // Dùng để đánh giá huy hiệu

    @Scheduled(cron = "0 * * * * ?")
    public void evaluateAchievementsForAllUsersDaily() {
        System.out.println("Bắt đầu tác vụ định kỳ: Đánh giá huy hiệu cho tất cả người dùng.");

        // Chỉ lấy những người dùng là MEMBER để đánh giá
        List<User> users = userService.getAllUsers().stream()
                .filter(user -> user.getRole() == UserRole.MEMBER)
                .collect(Collectors.toList());

        for (User user : users) {
            try {
                // Gọi phương thức đánh giá huy hiệu cho từng người dùng
                achievementService.evaluateAndAwardAchievements(user);
            } catch (Exception e) {
                // Ghi log lỗi để theo dõi nếu có vấn đề với một người dùng cụ thể
                System.err.println("Lỗi khi đánh giá huy hiệu cho user ID " + user.getId() + ": " + e.getMessage());
            }
        }

        System.out.println("Hoàn thành tác vụ định kỳ. Đã đánh giá cho " + users.size() + " người dùng.");
    }
}