package com.example.smoking.platform;

import com.example.smoking.platform.model.Achievement; // <-- Import này
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.service.AchievementService; // <-- Import này
import com.example.smoking.platform.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SmokingCessationPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmokingCessationPlatformApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(UserService userService, AchievementService achievementService) {
        return args -> {
            // Thêm user mẫu (nếu chưa có)
            userService.addSampleUsers();

            // Thêm huy hiệu mẫu (nếu chưa có)
            if (achievementService.getAllAchievements().isEmpty()) {
                System.out.println("Adding sample achievements...");
                achievementService.createAchievement(new Achievement("1-Day Free", "Đã không hút thuốc trong 1 ngày.", "/images/achievements/1day.png"));
                achievementService.createAchievement(new Achievement("1-Week Free", "Đã không hút thuốc trong 1 tuần.", "/images/achievements/1week.png"));
                achievementService.createAchievement(new Achievement("Money Saver", "Đã tiết kiệm được 100K VND từ việc cai thuốc.", "/images/achievements/money.webp"));
                System.out.println("Sample achievements added successfully.");
            }
        };
    }
}