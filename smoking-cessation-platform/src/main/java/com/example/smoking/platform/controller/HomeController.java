package com.example.smoking.platform.controller;

import com.example.smoking.platform.model.User;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
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

    @GetMapping("/login")
    public String login() {
        return "login"; // Hiển thị trang đăng nhập
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
            model.addAttribute("userId", currentUser.getId()); // Cần cho API JavaScript
            model.addAttribute("user", currentUser); // Đối tượng User đầy đủ
            model.addAttribute("userAchievements", achievementService.getUserAchievements(currentUser));

            Optional<SmokingLog> latestLogOptional = smokingLogService.findLatestSmokingLogByUser(currentUser);
            model.addAttribute("latestSmokingLog", latestLogOptional.orElse(null));

            model.addAttribute("smokeFreeDays", smokingLogService.calculateDaysWithoutSmoking(currentUser));
            model.addAttribute("moneySaved", smokingLogService.calculateMoneySaved(currentUser));
            model.addAttribute("healthMilestones", smokingLogService.getHealthProgress(currentUser)); // Từ file 1

            return "dashboard";

        } catch (Exception e) {
            e.printStackTrace(); // Ghi log chi tiết lỗi (nên thay bằng logging framework như SLF4J trong production)
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi tải dashboard.");
            return "redirect:/login";
        }
    }
}