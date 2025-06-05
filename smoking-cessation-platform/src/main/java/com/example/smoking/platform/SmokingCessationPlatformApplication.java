package com.example.smoking.platform;

import com.example.smoking.platform.service.UserService;
import org.springframework.boot.CommandLineRunner; // <-- Import này
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // <-- Import này

@SpringBootApplication
public class SmokingCessationPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmokingCessationPlatformApplication.class, args);
    }

    // Tạo một Bean CommandLineRunner để thêm dữ liệu mẫu khi ứng dụng khởi động
    @Bean
    public CommandLineRunner run(UserService userService) { // Spring sẽ tự động tiêm UserService vào đây
        return args -> {
            System.out.println("Executing CommandLineRunner: Adding sample users if needed...");
            userService.addSampleUsers(); // Gọi phương thức thêm người dùng mẫu
        };
    }
}