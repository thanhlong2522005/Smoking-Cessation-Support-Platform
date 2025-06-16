package com.example.smoking.platform.controller;

import com.example.smoking.platform.model.QuitPlan;
import com.example.smoking.platform.model.SmokingLog;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.service.QuitPlanService;
import com.example.smoking.platform.service.SmokingLogService;
import com.example.smoking.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
            private final double moneySaved;
            private final List<SmokingLogService.HealthMilestone> healthImprovement;

            public ProgressResponse(int daysWithoutSmoking, double moneySaved, List<SmokingLogService.HealthMilestone> healthImprovement) {
                this.daysWithoutSmoking = daysWithoutSmoking;
                this.moneySaved = moneySaved;
                this.healthImprovement = healthImprovement;
            }

            public int getDaysWithoutSmoking() { return daysWithoutSmoking; }
            public double getMoneySaved() { return moneySaved; }
            public List<SmokingLogService.HealthMilestone> getHealthImprovement() { return healthImprovement; }
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

        public static class WeeklyStatsResponse {
            private final List<String> labels;
            private final List<Integer> data;
            private final String title;

            public WeeklyStatsResponse(List<String> labels, List<Integer> data, String title) {
                this.labels = labels;
                this.data = data;
                this.title = title;
            }

            public List<String> getLabels() { return labels; }
            public List<Integer> getData() { return data; }
            public String getTitle() { return title; }
        }

        public static class CumulativeMoneySavedResponse {
            private final List<String> labels;
            private final List<Double> data;
            private final String title;

            public CumulativeMoneySavedResponse(List<String> labels, List<Double> data, String title) {
                this.labels = labels;
                this.data = data;
                this.title = title;
            }

            public List<String> getLabels() { return labels; }
            public List<Double> getData() { return data; }
            public String getTitle() { return title; }
        }

        // Helper method to get authenticated user
        private User getAuthenticatedUser(Authentication authentication) {
            String username = authentication.getName();
            return userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại: " + username));
        }

        @PostMapping("/log")
        public ResponseEntity<?> logSmoking(Authentication authentication, @RequestBody SmokingLog log) {
            User user = getAuthenticatedUser(authentication);
            log.setUser(user);
            try {
                SmokingLog savedLog = smokingLogService.save(log);
                return ResponseEntity.ok(new SmokingLogResponse("Tình trạng hút thuốc được ghi nhận", savedLog.getId()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(new SmokingLogResponse(e.getMessage(), null));
            }
        }

        @GetMapping("/logs")
        public ResponseEntity<?> getSmokingLogs(Authentication authentication) {
            User user = getAuthenticatedUser(authentication);
            return ResponseEntity.ok(smokingLogService.getLogsByUser(user));
        }

        @GetMapping("/logs/by-date")
        public ResponseEntity<?> getLogsByDate(Authentication authentication, @RequestParam String start, @RequestParam String end) {
            User user = getAuthenticatedUser(authentication);
            try {
                LocalDateTime startDate = LocalDateTime.parse(start);
                LocalDateTime endDate = LocalDateTime.parse(end);
                if (startDate.isAfter(endDate)) {
                    return ResponseEntity.badRequest().body(new SmokingLogResponse("Ngày bắt đầu phải trước ngày kết thúc", null));
                }
                return ResponseEntity.ok(smokingLogService.getLogsByUserAndDate(user, startDate, endDate));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new SmokingLogResponse("Định dạng ngày không hợp lệ: " + e.getMessage(), null));
            }
        }

        @GetMapping("/progress")
        public ResponseEntity<?> getProgress(Authentication authentication) {
            User user = getAuthenticatedUser(authentication);
            int daysWithoutSmoking = smokingLogService.calculateDaysWithoutSmoking(user);
            double moneySaved = smokingLogService.calculateMoneySaved(user);
            List<SmokingLogService.HealthMilestone> healthImprovement = smokingLogService.estimateHealthImprovement(user);
            return ResponseEntity.ok(new ProgressResponse(daysWithoutSmoking, moneySaved, healthImprovement));
        }

        // Endpoint cho dữ liệu biểu đồ tuần (từ SmokingStatsApiController)
        @GetMapping("/stats/weekly-chart-data")
        public ResponseEntity<?> getSmokingStatsByWeekChartData(Authentication authentication) {
            User user = getAuthenticatedUser(authentication);
            Map<String, Integer> rawStats = smokingLogService.getSmokingStatsByWeek(user);
            List<String> labels = new ArrayList<>(rawStats.keySet());
            List<Integer> data = new ArrayList<>(rawStats.values());
            return ResponseEntity.ok(new WeeklyStatsResponse(labels, data, "Số điếu thuốc hút trong 4 tuần gần nhất"));
        }

        // Endpoint cho dữ liệu biểu đồ tiền tiết kiệm tích lũy (từ SmokingStatsApiController)
        @GetMapping("/stats/money-saved-chart-data")
        public ResponseEntity<?> getMoneySavedChartData(Authentication authentication,
                                                        @RequestParam(defaultValue = "90") int daysToLookBack) {
            User user = getAuthenticatedUser(authentication);
            Map<LocalDate, Double> rawStats = smokingLogService.getCumulativeMoneySavedChartData(user, daysToLookBack);
            List<LocalDate> sortedDates = rawStats.keySet().stream().sorted().collect(Collectors.toList());
            List<String> labels = sortedDates.stream()
                    .map(date -> date.format(DateTimeFormatter.ofPattern("dd/MM")))
                    .collect(Collectors.toList());
            List<Double> data = sortedDates.stream().map(rawStats::get).collect(Collectors.toList());
            return ResponseEntity.ok(new CumulativeMoneySavedResponse(labels, data, "Tiền tiết kiệm tích lũy"));
        }

        @GetMapping("/stats/total-cost-spent")
        public ResponseEntity<?> getTotalCostSpent(Authentication authentication) {
            User user = getAuthenticatedUser(authentication);
            return ResponseEntity.ok(smokingLogService.calculateTotalCost(user));
        }

        @PostMapping("/quit-plan")
        public ResponseEntity<?> createQuitPlan(Authentication authentication, @RequestBody QuitPlan plan) {
            User user = getAuthenticatedUser(authentication);
            plan.setUser(user);
            try {
                QuitPlan savedPlan = quitPlanService.save(plan);
                return ResponseEntity.ok(new QuitPlanResponse("Kế hoạch cai thuốc được tạo thành công", savedPlan.getId()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(new QuitPlanResponse(e.getMessage(), null));
            }
        }

        @GetMapping("/quit-plan")
        public ResponseEntity<?> getQuitPlans(Authentication authentication) {
            User user = getAuthenticatedUser(authentication);
            return ResponseEntity.ok(quitPlanService.getPlansByUser(user));
        }

        @PostMapping("/quit-plan/{planId}/status")
        public ResponseEntity<QuitPlanResponse> updateQuitPlanStatus(Authentication authentication, @PathVariable Long planId, @RequestBody QuitPlan.Status status) {
            User user = getAuthenticatedUser(authentication);
            QuitPlan existingPlan = quitPlanService.findById(planId)
                    .orElseThrow(() -> new RuntimeException("Kế hoạch cai thuốc không tồn tại với ID: " + planId));
            if (!existingPlan.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new QuitPlanResponse("Bạn không có quyền cập nhật kế hoạch này", null));
            }
            try {
                QuitPlan updatedPlan = quitPlanService.updateStatus(planId, status)
                        .orElseThrow(() -> new RuntimeException("Cập nhật trạng thái kế hoạch thất bại"));
                return ResponseEntity.ok(new QuitPlanResponse("Cập nhật trạng thái kế hoạch thành công", updatedPlan.getId()));
            } catch (RuntimeException e) { // Chỉ giữ RuntimeException
                return ResponseEntity.badRequest()
                        .body(new QuitPlanResponse(e.getMessage(), null));
            }
        }
    }

    // Web Controller (Thymeleaf views)
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
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại với ID: " + userId));
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
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại với ID: " + userId));
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