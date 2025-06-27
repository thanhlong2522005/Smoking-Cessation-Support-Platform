package com.example.smoking.platform.controller.user;

import com.example.smoking.platform.model.Feedback;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.service.FeedbackService;
import com.example.smoking.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional; // <-- Thêm import này

@Controller
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserService userService;

    @GetMapping("/feedback")
    public String showFeedbackForm() {
        return "feedback";
    }

    @PostMapping("/feedback")
    public String submitFeedback(@RequestParam("content") String content,
                                 RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<User> currentUserOptional = userService.getUserByUsername(currentUsername); // <-- Gọi đúng tên phương thức và kiểu trả về

        if (currentUserOptional.isEmpty()) { // <-- Kiểm tra Optional có rỗng không
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.");
            return "redirect:/feedback";
        }

        User currentUser = currentUserOptional.get(); // <-- Lấy đối tượng User từ Optional

        Feedback feedback = new Feedback(content, currentUser);
        feedbackService.saveFeedback(feedback);

        redirectAttributes.addFlashAttribute("successMessage", "Phản hồi của bạn đã được gửi thành công!");
        return "redirect:/feedback";
    }
}