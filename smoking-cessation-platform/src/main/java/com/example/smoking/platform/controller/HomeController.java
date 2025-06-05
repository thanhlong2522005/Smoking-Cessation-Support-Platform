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
}