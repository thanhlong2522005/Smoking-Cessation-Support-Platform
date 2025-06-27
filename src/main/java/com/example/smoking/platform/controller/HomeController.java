package com.example.smoking.platform.controller;

import com.example.smoking.platform.model.User;
import com.example.smoking.platform.model.SmokingLog;
import com.example.smoking.platform.model.Motivation;
import com.example.smoking.platform.service.MotivationService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private SmokingLogService smokingLogService;

    @Autowired
    private MotivationService motivationService;

    // ✅ Trang chính Dashboard
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

            // ➕ Dữ liệu người dùng
            model.addAttribute("userId", currentUser.getId());
            model.addAttribute("userRole", currentUser.getRole().toString());
            model.addAttribute("user", currentUser);
            model.addAttribute("userAchievements", achievementService.getUserAchievements(currentUser));

            // ➕ Lịch sử và tiến trình
            Optional<SmokingLog> latestLogOptional = smokingLogService.findLatestSmokingLogByUser(currentUser);
            model.addAttribute("latestSmokingLog", latestLogOptional.orElse(null));
            model.addAttribute("smokeFreeDays", smokingLogService.calculateDaysWithoutSmoking(currentUser));
            model.addAttribute("moneySaved", smokingLogService.calculateMoneySaved(currentUser));
            model.addAttribute("healthMilestones", smokingLogService.getHealthProgress(currentUser));

            // ✅ Thêm quote động viên
            Motivation quote = motivationService.getRandomMotivation();
            model.addAttribute("quote", quote);
            model.addAttribute("newMotivation", new Motivation());

            return "dashboard";

        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi (nên dùng logger trong production)
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi tải dashboard.");
            return "redirect:/login";
        }
    }

    // ✅ Gửi lời động viên mới
    @PostMapping("/dashboard/addMotivation")
    public String addMotivation(@ModelAttribute("newMotivation") Motivation newMotivation,
                                Principal principal) {
        newMotivation.setAuthor(principal != null ? principal.getName() : "Ẩn danh");
        motivationService.save(newMotivation);
        return "redirect:/dashboard";
    }

    // ✅ Trang chủ giới thiệu
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User()); // để dùng th:object="${user}"
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user,
                              @RequestParam("confirmPassword") String confirmPassword,
                              RedirectAttributes redirectAttributes) {
        try {
            if (!user.getPassword().equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu không khớp.");
                return "redirect:/register";
            }

            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi đăng ký.");
            return "redirect:/register";
        }
    }




    // ✅ Trang đăng nhập
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
