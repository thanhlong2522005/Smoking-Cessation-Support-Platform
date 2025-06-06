package com.example.smoking.platform.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.smoking.platform.model.SmokingLog;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.service.SmokingLogService;
import com.example.smoking.platform.service.UserService;

@Controller
@RequestMapping("/smoking")
public class SmokingController {

    @Autowired
    private SmokingLogService smokingLogService;

    @Autowired
    private UserService userService;

    // API RESTful
    @RestController
    @RequestMapping("/api/smoking")
    public class ApiController {

        // DTO cho response
        public static class SmokingLogResponse {
            private String message;
            private Long logId;

            public SmokingLogResponse(String message, Long logId) {
                this.message = message;
                this.logId = logId;
            }

            public String getMessage() { return message; }
            public Long getLogId() { return logId; }
        }

        public static class ProgressResponse {
            private int daysWithoutSmoking;
            private double totalCost;
            private String healthImprovement;

            public ProgressResponse(int daysWithoutSmoking, double totalCost, String healthImprovement) {
                this.daysWithoutSmoking = daysWithoutSmoking;
                this.totalCost = totalCost;
                this.healthImprovement = healthImprovement;
            }

            public int getDaysWithoutSmoking() { return daysWithoutSmoking; }
            public double getTotalCost() { return totalCost; }
            public String getHealthImprovement() { return healthImprovement; }
        }

        @PreAuthorize("#userId == authentication.principal.id")
        @PostMapping("/log")
        public ResponseEntity<?> logSmoking(@RequestParam Long userId, @RequestBody SmokingLog log) {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            log.setUser(user);
            try {
                SmokingLog savedLog = smokingLogService.save(log);
                return ResponseEntity.ok(new SmokingLogResponse("Tình trạng hút thuốc được ghi nhận", savedLog.getId()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(new SmokingLogResponse(e.getMessage(), null));
            }
        }

        @PreAuthorize("#userId == authentication.principal.id")
        @GetMapping("/logs")
        public ResponseEntity<?> getSmokingLogs(@RequestParam Long userId) {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            return ResponseEntity.ok(smokingLogService.getLogsByUser(user));
        }

        @PreAuthorize("#userId == authentication.principal.id")
        @GetMapping("/logs/by-date")
        public ResponseEntity<?> getLogsByDate(@RequestParam Long userId, @RequestParam String start, @RequestParam String end) {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            try {
                LocalDateTime startDate = LocalDateTime.parse(start);
                LocalDateTime endDate = LocalDateTime.parse(end);
                if (startDate.isAfter(endDate)) {
                    return ResponseEntity.badRequest().body(new SmokingLogResponse("Start date must be before end date", null));
                }
                return ResponseEntity.ok(smokingLogService.getLogsByUserAndDate(user, startDate, endDate));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new SmokingLogResponse("Invalid date format: " + e.getMessage(), null));
            }
        }

        @PreAuthorize("#userId == authentication.principal.id")
        @GetMapping("/progress")
        public ResponseEntity<?> getProgress(@RequestParam Long userId) {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            int daysWithoutSmoking = smokingLogService.calculateDaysWithoutSmoking(user);
            double totalCost = smokingLogService.calculateTotalCost(user);
            String healthImprovement = smokingLogService.estimateHealthImprovement(user);
            return ResponseEntity.ok(new ProgressResponse(daysWithoutSmoking, totalCost, healthImprovement));
        }

        @PreAuthorize("#userId == authentication.principal.id")
        @GetMapping("/stats/weekly")
        public ResponseEntity<?> getSmokingStatsByWeek(@RequestParam Long userId) {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            return ResponseEntity.ok(smokingLogService.getSmokingStatsByWeek(user));
        }

        @PreAuthorize("#userId == authentication.principal.id")
        @GetMapping("/stats/total-cost")
        public ResponseEntity<?> getTotalCost(@RequestParam Long userId) {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            return ResponseEntity.ok(smokingLogService.calculateTotalCost(user));
        }
    }

    // Web Controller
    @GetMapping("/log")
    public String showLogForm(Model model, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        model.addAttribute("userId", userId);
        model.addAttribute("log", new SmokingLog());
        return "log";
    }

    @PostMapping("/log")
    public String saveLog(@ModelAttribute("log") SmokingLog log, @RequestParam Long userId, Model model) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        log.setUser(user);
        try {
            smokingLogService.save(log);
            model.addAttribute("message", "Ghi nhận thành công!");
            model.addAttribute("success", true);
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", e.getMessage());
            model.addAttribute("success", false);
        }
        model.addAttribute("userId", userId);
        return "log";
    }

    @GetMapping("/logs")
    public String showLogs(Model model, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        model.addAttribute("userId", userId);
        return "logs";
    }
}