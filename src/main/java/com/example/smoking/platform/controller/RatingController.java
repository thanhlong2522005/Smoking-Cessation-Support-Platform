package com.example.smoking.platform.controller;

import com.example.smoking.platform.model.User;
import com.example.smoking.platform.service.RatingService;
import com.example.smoking.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private UserService userService;

    @GetMapping("/rate")
    public String showRatingForm() {
        return "rate"; // Giao diện đánh giá
    }

    @PostMapping("/rate")
    public String submitRating(@RequestParam int stars,
                                @RequestParam(required = false) String comment,
                                Authentication authentication,
                                Model model) {

        String username = authentication.getName();
        User user = userService.getUserByUsername(username).orElse(null);

        if (user != null) {
            boolean success = ratingService.submitRating(user, stars, comment);
            if (success) {
                model.addAttribute("successMessage", "Cảm ơn bạn đã đánh giá!");
            } else {
                model.addAttribute("errorMessage", "Bạn đã đánh giá rồi. Mỗi người chỉ được đánh giá một lần.");
            }
        }

        return "rate";
    }
}
