package com.example.smoking.platform.controller;

import com.example.smoking.platform.model.QuitPlan;
import com.example.smoking.platform.model.SmokingLog;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.service.QuitPlanService;
import com.example.smoking.platform.service.SmokingLogService;
import com.example.smoking.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Controller
@RequestMapping("/smoking")
public class SmokingController {

    @Autowired
    private SmokingLogService smokingLogService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuitPlanService quitPlanService;

    // API RESTful
    @RestController
    @RequestMapping("/api/smoking")
    public class ApiController {

        // DTO cho response
        public static class SmokingLogResponse {
            private final String message;
            private final Long logId;

            public SmokingLogResponse(String message, Long logId) {
                this.message = message;
                this.logId = logId;
            }

            public String getMessage() { return message; }
            public Long getLogId() { return logId; }
        }

        public static class ProgressResponse {
            private final int daysWithoutSmoking;
            private final double totalCost;
            private final String healthImprovement;

            public ProgressResponse(int daysWithoutSmoking, double totalCost, String healthImprovement) {
                this.daysWithoutSmoking = daysWithoutSmoking;
                this.totalCost = totalCost;
                this.healthImprovement = healthImprovement;
            }

            public int getDaysWithoutSmoking() { return daysWithoutSmoking; }
            public double getTotalCost() { return totalCost; }
            public String getHealthImprovement() { return healthImprovement; }
        }

        public static class QuitPlanResponse {
            private final String message;
            private final Long planId;

            public QuitPlanResponse(String message, Long planId) {
                this.message = message;
                this.planId = planId;
            }

            public String getMessage() { return message; }
            public Long getPlanId() { return planId; }
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

        @PreAuthorize("#userId == authentication.principal.id")
        @PostMapping("/quit-plan")
        public ResponseEntity<?> createQuitPlan(@RequestParam Long userId, @RequestBody QuitPlan plan) {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            plan.setUser(user);
            try {
                QuitPlan savedPlan = quitPlanService.save(plan);
                return ResponseEntity.ok(new QuitPlanResponse("Kế hoạch cai thuốc được tạo thành công", savedPlan.getId()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(new QuitPlanResponse(e.getMessage(), null));
            }
        }

        @PreAuthorize("#userId == authentication.principal.id")
        @GetMapping("/quit-plan")
        public ResponseEntity<?> getQuitPlans(@RequestParam Long userId) {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            return ResponseEntity.ok(quitPlanService.getPlansByUser(user));
        }

        @PreAuthorize("#userId == authentication.principal.id")
        @PostMapping("/quit-plan/{planId}/status")
        public ResponseEntity<?> updateQuitPlanStatus(@RequestParam Long userId, @PathVariable Long planId, @RequestBody QuitPlan.Status status) {
            try {
                QuitPlan updatedPlan = quitPlanService.updateStatus(planId, status);
                return ResponseEntity.ok(new QuitPlanResponse("Cập nhật trạng thái kế hoạch thành công", updatedPlan.getId()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(new QuitPlanResponse(e.getMessage(), null));
            }
        }
    }

    // Web Controller
    // Đã sửa để khớp với tên file HTML: log-smoking.html và view-logs.html
    @GetMapping("/log")
    public String showLogForm(Model model, Authentication authentication) {
        String currentUsername = authentication.getName();
        Optional<User> currentUserOptional = userService.getUserByUsername(currentUsername);

        if (currentUserOptional.isEmpty()) {
            return "redirect:/login?error=userNotFound";
        }

        User currentUser = currentUserOptional.get();
        model.addAttribute("userId", currentUser.getId());
        model.addAttribute("log", new SmokingLog());
        return "log-smoking"; 
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
        return "log-smoking"; 
    }

    @GetMapping("/logs")
    public String showLogs(Model model, Authentication authentication) {
        String currentUsername = authentication.getName();
        Optional<User> currentUserOptional = userService.getUserByUsername(currentUsername);

        if (currentUserOptional.isEmpty()) {
            return "redirect:/login?error=userNotFound";
        }

        User currentUser = currentUserOptional.get();
        model.addAttribute("userId", currentUser.getId());
        // Bạn có thể muốn thêm danh sách logs vào model ở đây nếu trang logs hiển thị trực tiếp dữ liệu
        // model.addAttribute("smokingLogs", smokingLogService.getLogsByUser(currentUser));
        return "view-logs"; 
    }

    @GetMapping("/quit-plan")
    public String showQuitPlanForm(Model model, Authentication authentication) {
        String currentUsername = authentication.getName();
        Optional<User> currentUserOptional = userService.getUserByUsername(currentUsername);

        if (currentUserOptional.isEmpty()) {
            return "redirect:/login?error=userNotFound";
        }

        User currentUser = currentUserOptional.get();
        model.addAttribute("userId", currentUser.getId());
        model.addAttribute("quitPlan", new QuitPlan());
        model.addAttribute("statuses", QuitPlan.Status.values());
        model.addAttribute("suggestedPhases", Arrays.asList(
            "Giai đoạn 1: Giảm số lượng điếu thuốc mỗi ngày",
            "Giai đoạn 2: Thay thế thói quen hút thuốc bằng hoạt động khác",
            "Giai đoạn 3: Ngừng hút thuốc hoàn toàn"
        ));
        return "quit-plan"; 
    }

    @PostMapping("/quit-plan")
    public String saveQuitPlan(@ModelAttribute("quitPlan") QuitPlan quitPlan, @RequestParam Long userId, Model model) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        quitPlan.setUser(user);
        try {
            quitPlanService.save(quitPlan);
            model.addAttribute("message", "Kế hoạch cai thuốc được tạo thành công!");
            model.addAttribute("success", true);
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", e.getMessage());
            model.addAttribute("success", false);
        }
        model.addAttribute("userId", userId);
        model.addAttribute("statuses", QuitPlan.Status.values());
        model.addAttribute("suggestedPhases", Arrays.asList(
            "Giai đoạn 1: Giảm số lượng điếu thuốc mỗi ngày",
            "Giai đoạn 2: Thay thế thói quen hút thuốc bằng hoạt động khác",
            "Giai đoạn 3: Ngừng hút thuốc hoàn toàn"
        ));
        return "quit-plan"; 
    }

    @GetMapping("/quit-plans")
    public String showQuitPlans(Model model, Authentication authentication) {
        String currentUsername = authentication.getName();
        Optional<User> currentUserOptional = userService.getUserByUsername(currentUsername);

        if (currentUserOptional.isEmpty()) {
            return "redirect:/login?error=userNotFound";
        }

        User currentUser = currentUserOptional.get();
        model.addAttribute("userId", currentUser.getId());
        model.addAttribute("quitPlans", quitPlanService.getPlansByUser(currentUser));
        return "quit-plans"; 
    }
}