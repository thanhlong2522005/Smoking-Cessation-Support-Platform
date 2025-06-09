package com.example.smoking.platform.controller;

import java.util.Optional;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.model.UserRole;
import com.example.smoking.platform.service.AchievementService; 
import com.example.smoking.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; 
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller // Đánh dấu đây là một Spring MVC Controller
public class HomeController {

    @Autowired
    private UserService userService;
    @Autowired // <-- Thêm dòng này
    private AchievementService achievementService;

    @GetMapping("/")
    public String index() {
        return "index"; // Trang chủ giới thiệu
    }

    @GetMapping("/login") // Hiển thị trang đăng nhập
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<User> currentUserOptional = userService.getUserByUsername(currentUsername);

        if (currentUserOptional.isEmpty()) {
        } else {
            User currentUser = currentUserOptional.get();
            model.addAttribute("userAchievements", achievementService.getUserAchievements(currentUser));
        }
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