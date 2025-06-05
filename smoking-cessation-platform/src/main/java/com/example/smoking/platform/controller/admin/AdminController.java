package com.example.smoking.platform.controller.admin;

import com.example.smoking.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin") // Tất cả các request trong controller này sẽ bắt đầu bằng /admin
public class AdminController {

    @Autowired
    private UserService userService; // Thêm UserService để lấy thông tin người dùng

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // Lấy tất cả người dùng để hiển thị trong dashboard
        model.addAttribute("users", userService.getAllUsers());
        // Bạn có thể thêm các thông tin khác vào model ở đây (ví dụ: feedback, thống kê)
        return "admin/dashboard"; // Trả về view admin/dashboard.html
    }

    // Các phương thức khác cho admin (quản lý user, feedback, v.v.) sẽ được thêm vào đây
}