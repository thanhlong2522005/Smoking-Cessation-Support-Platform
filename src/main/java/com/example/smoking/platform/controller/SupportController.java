package com.example.smoking.platform.controller;

import com.example.smoking.platform.service.SupportRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class SupportController {

    @Autowired
    private SupportRequestService supportService;

    @GetMapping("/contact-admin")
    public String showForm() {
        return "contact-admin"; // File contact-admin.html trong templates
    }

    @PostMapping("/contact-admin")
    public String submitForm(@RequestParam String subject,
                             @RequestParam String type,
                             @RequestParam String message,
                             Principal principal,
                             Model model) {
        String username = (principal != null) ? principal.getName() : "anonymous";
        supportService.saveNewRequest(subject, type, message, username);
        model.addAttribute("message", "Gửi yêu cầu thành công!");
        return "contact-admin";
    }
}
