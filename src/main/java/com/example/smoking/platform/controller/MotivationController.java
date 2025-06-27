package com.example.smoking.platform.controller;

import com.example.smoking.platform.model.Motivation;
import com.example.smoking.platform.service.MotivationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class MotivationController {

    @Autowired
    private MotivationService motivationService;

    @GetMapping("/motivation")
    public String showMotivation(Model model) {
        Motivation randomMotivation = motivationService.getRandomMotivation();
        model.addAttribute("quote", randomMotivation);
        model.addAttribute("newMotivation", new Motivation());
        return "motivation";
    }

    @PostMapping("/motivation")
    public String addMotivation(@ModelAttribute("newMotivation") Motivation newMotivation,
                                Principal principal) {
        newMotivation.setAuthor(principal != null ? principal.getName() : "Ẩn danh");
        motivationService.save(newMotivation);
        return "redirect:/motivation";
    }
}

