package com.example.smoking.platform;

import com.example.smoking.platform.model.Achievement; // <-- Import này
import com.example.smoking.platform.model.Achievement.AchievementType;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.service.AchievementService; // <-- Import này
import com.example.smoking.platform.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
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
            if (achievementService.getAllAchievements().isEmpty()) { // Kiểm tra nếu DB rỗng huy hiệu
                System.out.println("Adding sample achievements...");
                // Huy hiệu thời gian
                achievementService.createAchievement(new Achievement(null, "1-Day Free", "Đã không hút thuốc trong 1 ngày.", "/images/achievements/1day.png", AchievementType.TIME_BASED));
                achievementService.createAchievement(new Achievement(null, "1-Week Free", "Đã không hút thuốc trong 1 tuần.", "/images/achievements/1week.png", AchievementType.TIME_BASED));
                achievementService.createAchievement(new Achievement(null, "1-Month Free", "Đã không hút thuốc trong 1 tháng.", "/images/achievements/1month.png", AchievementType.TIME_BASED));
                achievementService.createAchievement(new Achievement(null, "3-Months Free", "Đã không hút thuốc trong 3 tháng.", "/images/achievements/3months.png", AchievementType.TIME_BASED));
                achievementService.createAchievement(new Achievement(null, "6-Months Free", "Đã không hút thuốc trong 6 tháng.", "/images/achievements/6months.png", AchievementType.TIME_BASED));
                achievementService.createAchievement(new Achievement(null, "1-Year Free", "Đã không hút thuốc trong 1 năm.", "/images/achievements/1year.png", AchievementType.TIME_BASED));

                // Huy hiệu tiền tiết kiệm (điền giá trị null cho ID)
                achievementService.createAchievement(new Achievement(null, "Money Saver I", "Đã tiết kiệm được 100K VND từ việc cai thuốc.", "/images/achievements/money100k.png", AchievementType.MONEY_BASED));
                achievementService.createAchievement(new Achievement(null, "Money Saver II", "Đã tiết kiệm được 500K VND từ việc cai thuốc.", "/images/achievements/money500k.png", AchievementType.MONEY_BASED));
                achievementService.createAchievement(new Achievement(null, "Money Saver III", "Đã tiết kiệm được 1 triệu VND từ việc cai thuốc.", "/images/achievements/money1M.png", AchievementType.MONEY_BASED));

                // Huy hiệu mục tiêu
                achievementService.createAchievement(new Achievement(null, "Quit Goal Achieved", "Đã hoàn thành mục tiêu cai thuốc đã đặt ra.", "/images/achievements/goal.png", AchievementType.GOAL_BASED));

                System.out.println("Sample achievements added successfully.");
            }
        };
    }
}