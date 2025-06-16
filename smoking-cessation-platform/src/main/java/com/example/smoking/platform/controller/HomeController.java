package com.example.smoking.platform.controller;

import java.time.LocalDateTime;
import java.util.Optional;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.model.UserRole;
import com.example.smoking.platform.model.SmokingLog;
import com.example.smoking.platform.service.AchievementService;
import com.example.smoking.platform.service.SmokingLogService;
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
    @Autowired 
    private AchievementService achievementService;
    @Autowired 
    private SmokingLogService smokingLogService;

    @GetMapping("/")
    public String index() {
        return "index"; // Trang chủ giới thiệu
    }

    @GetMapping("/login") // Hiển thị trang đăng nhập
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
public String dashboard(Model model, RedirectAttributes redirectAttributes) {
    try {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<User> currentUserOptional = userService.getUserByUsername(currentUsername);

        if (currentUserOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng.");
            return "redirect:/login";
        }

        User currentUser = currentUserOptional.get();

        // Truyền dữ liệu cho frontend
        model.addAttribute("userId", currentUser.getId());
        model.addAttribute("userAchievements", achievementService.getUserAchievements(currentUser));

        Optional<SmokingLog> latestLogOptional = smokingLogService.findLatestSmokingLogByUser(currentUser);
        model.addAttribute("latestSmokingLog", latestLogOptional.orElse(null));

        model.addAttribute("smokeFreeDays", smokingLogService.calculateDaysWithoutSmoking(currentUser));
        model.addAttribute("moneySaved", smokingLogService.calculateMoneySaved(currentUser));
        model.addAttribute("user", currentUser);

        return "dashboard";

    } catch (Exception e) {
        e.printStackTrace(); // Ghi log chi tiết lỗi
        redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi tải dashboard.");
        return "redirect:/login"; // hoặc chuyển đến trang lỗi riêng nếu có
    }
}
}