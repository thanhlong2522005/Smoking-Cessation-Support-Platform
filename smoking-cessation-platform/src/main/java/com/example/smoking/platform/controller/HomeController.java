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
public String dashboard(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = authentication.getName();

    Optional<User> currentUserOptional = userService.getUserByUsername(currentUsername);

    if (currentUserOptional.isEmpty()) {
        // Xử lý trường hợp người dùng không tìm thấy (có thể redirect hoặc hiển thị lỗi)
        // Hiện tại, khối này đang trống, nên nếu không tìm thấy user, các attribute dưới sẽ không được thêm.
        // Có thể thêm logging hoặc redirect về trang login với thông báo lỗi
        // return "redirect:/login?error=userNotFound";
    } else {
        User currentUser = currentUserOptional.get();

        // THÊM userId vào model để JavaScript có thể sử dụng cho API calls
        model.addAttribute("userId", currentUser.getId()); // <--- THÊM DÒNG NÀY

        model.addAttribute("userAchievements", achievementService.getUserAchievements(currentUser));

        // Lấy thông tin cho popup thông báo và hiển thị trên dashboard
        Optional<SmokingLog> latestLogOptional = smokingLogService.findLatestSmokingLogByUser(currentUser);
        model.addAttribute("latestSmokingLog", latestLogOptional.orElse(null));

        // Sử dụng các phương thức tính toán mới nhất và nhất quán với tên biến trong frontend
        model.addAttribute("smokeFreeDays", (long) smokingLogService.calculateDaysWithoutSmoking(currentUser)); // <--- ĐÃ SỬA
        model.addAttribute("moneySaved", smokingLogService.calculateMoneySaved(currentUser)); // <--- ĐÃ SỬA VÀ ĐỔI TÊN

        // Thêm các thuộc tính người dùng cần thiết cho dashboard nếu chưa có
        model.addAttribute("user", currentUser); // Giữ lại nếu các phần khác của dashboard cần đối tượng user
    }
    return "dashboard";
}
}