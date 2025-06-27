package com.example.smoking.platform.controller.admin;

import com.example.smoking.platform.model.Achievement;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.model.UserAchievement;
import com.example.smoking.platform.model.UserRole;
import com.example.smoking.platform.service.AchievementService;
import com.example.smoking.platform.service.FeedbackService;
import com.example.smoking.platform.service.RatingService;
import com.example.smoking.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List; 
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private AchievementService achievementService;
    @Autowired
    private RatingService ratingService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        return "admin/dashboard";
    }

    // Phương thức để đánh dấu feedback là đã đọc
    @GetMapping("/feedback/mark-read/{id}") // Dùng GetMapping cho đơn giản, POST sẽ tốt hơn trong thực tế
    public String markFeedbackAsRead(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        feedbackService.markAsRead(id);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback đã được đánh dấu là đã đọc.");
        return "redirect:/admin/feedbacks";
    }

    // Phương thức để xóa feedback
    @GetMapping("/feedback/delete/{id}") // Dùng GetMapping cho đơn giản, POST sẽ tốt hơn trong thực tế
    public String deleteFeedback(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        feedbackService.deleteFeedback(id);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback đã được xóa.");
        return "redirect:/admin/feedbacks";
    }
    
    // Hiển thị form tạo huy hiệu mới
    @GetMapping("/achievements/new")
    public String showCreateAchievementForm(Model model) {
        model.addAttribute("achievement", new Achievement());
        return "admin/create_achievement"; // Trang form tạo huy hiệu
    }
    // Xử lý tạo huy hiệu mới
    @PostMapping("/achievements")
    public String createAchievement(@RequestParam String name,
                                    @RequestParam String description,
                                    @RequestParam(required = false) String iconUrl,
                                    RedirectAttributes redirectAttributes) {
        try {
            Achievement newAchievement = new Achievement(name, description, iconUrl);
            achievementService.createAchievement(newAchievement);
            redirectAttributes.addFlashAttribute("successMessage", "Huy hiệu '" + name + "' đã được tạo thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/achievements"; // Quay lại dashboard
    }


    // Xóa huy hiệu
    @GetMapping("/achievements/delete/{id}") // Dùng GetMapping cho đơn giản
    public String deleteAchievement(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            achievementService.deleteAchievement(id);
            redirectAttributes.addFlashAttribute("successMessage", "Huy hiệu đã được xóa.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa huy hiệu: " + e.getMessage());
        }
        return "redirect:/admin/achievements";
    }

    @GetMapping("/report")
    public String viewSystemReport() {
        return "admin/report";
    }

    @GetMapping("/users")
    public String viewUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/feedbacks")
    public String viewFeedbacks(Model model) {
        model.addAttribute("feedbacks", feedbackService.getAllFeedbacks());
        return "admin/feedbacks";
    }

    @GetMapping("/achievements")
    public String viewAchievements(Model model) {
        model.addAttribute("achievements", achievementService.getAllAchievements());
        return "admin/achievements";
    }

    @GetMapping("/ratings")
    public String viewRatings(Model model) {
        model.addAttribute("ratings", ratingService.getAllRatings());
        model.addAttribute("averageStars", ratingService.calculateAverageStars());
        return "admin/ratings";
    }


}