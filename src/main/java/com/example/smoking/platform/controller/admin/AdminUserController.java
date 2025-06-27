package com.example.smoking.platform.controller.admin;

import com.example.smoking.platform.model.User;
import com.example.smoking.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/admin/users-list")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin-user-list";
    }

     // Xem chi tiết người dùng
    @GetMapping("/admin/user/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElse(null);
        model.addAttribute("user", user);
        return "admin-user-profile";
    }

    // Xoá người dùng (tuỳ chọn)
    @GetMapping("/admin/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users-list";
    }
}

