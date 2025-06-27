package com.example.smoking.platform.controller.user;

import com.example.smoking.platform.model.User;
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

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class UserProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/user-profile")
    public String showUserProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<User> currentUserOptional = userService.getUserByUsername(currentUsername);

        if (currentUserOptional.isEmpty()) {
            // Xử lý trường hợp không tìm thấy người dùng (ví dụ: chuyển hướng đến trang đăng nhập)
            return "redirect:/login";
        }

        User user = currentUserOptional.get();
        model.addAttribute("user", user); // Truyền đối tượng user vào model
        model.addAttribute("successMessage", model.asMap().get("successMessage")); // Lấy tin nhắn thành công từ redirect
        model.addAttribute("errorMessage", model.asMap().get("errorMessage"));   // Lấy tin nhắn lỗi từ redirect
        return "user-profile"; // Trả về tên file HTML
    }

    @PostMapping("/user-profile")
    public String updateUserProfile(@ModelAttribute User userUpdates, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<User> currentUserOptional = userService.getUserByUsername(currentUsername);

        if (currentUserOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.");
            return "redirect:/login";
        }

        User currentUser = currentUserOptional.get();
        Long userId = currentUser.getId(); // Lấy ID của người dùng hiện tại

        try {
            // Cập nhật các trường liên quan đến cai thuốc
            userService.updateCessationInfo(
                userId,
                userUpdates.getQuitStartDate(),
                userUpdates.getCigarettesPerDay(),
                userUpdates.getCostPerPack(),
                userUpdates.getCessationGoal(), // Thêm trường này
                userUpdates.getExpectedQuitDate() // Thêm trường này
            );

            // Bạn có thể cập nhật các trường khác như fullName, gender, dateOfBirth nếu muốn
            // Ví dụ:
            // currentUser.setFullName(userUpdates.getFullName());
            // currentUser.setGender(userUpdates.getGender());
            // currentUser.setDateOfBirth(userUpdates.getDateOfBirth());
            // userService.save(currentUser); // Nếu bạn có một phương thức save chung trong UserService

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi cập nhật hồ sơ: " + e.getMessage());
        }

        return "redirect:/user-profile"; // Chuyển hướng về trang hồ sơ sau khi cập nhật
    }
}