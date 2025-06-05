package com.example.smoking.platform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Đánh dấu đây là một Spring MVC Controller
public class HomeController {


    @GetMapping("/")
    public String home() {
        return "index"; // Trang chủ giới thiệu
    }

    @GetMapping("/login") // Hiển thị trang đăng nhập
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard") // Trang sau khi đăng nhập thành công
    public String dashboard() {
        return "dashboard";
    }
    
    @GetMapping("/log-smoking") // Trang ghi nhận tình trạng hút thuốc
    public String logSmoking() {
        return "log-smoking";
    }

    @GetMapping("/view-logs") // Trang xem danh sách log hút thuốc
    public String viewLogs() {
        return "view-logs";
    }
}