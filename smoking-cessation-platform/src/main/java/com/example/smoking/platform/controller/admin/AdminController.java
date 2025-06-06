package com.example.smoking.platform.controller.admin;

import com.example.smoking.platform.service.FeedbackService;
import com.example.smoking.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("feedbacks", feedbackService.getAllFeedbacks());
        return "admin/dashboard";
    }

    // Phương thức để đánh dấu feedback là đã đọc
    @GetMapping("/feedback/mark-read/{id}") // Dùng GetMapping cho đơn giản, POST sẽ tốt hơn trong thực tế
    public String markFeedbackAsRead(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        feedbackService.markAsRead(id);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback đã được đánh dấu là đã đọc.");
        return "redirect:/admin/dashboard";
    }

    // Phương thức để xóa feedback
    @GetMapping("/feedback/delete/{id}") // Dùng GetMapping cho đơn giản, POST sẽ tốt hơn trong thực tế
    public String deleteFeedback(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        feedbackService.deleteFeedback(id);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback đã được xóa.");
        return "redirect:/admin/dashboard";
    }
}