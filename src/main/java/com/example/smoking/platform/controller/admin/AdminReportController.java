package com.example.smoking.platform.controller.admin;

import com.example.smoking.platform.model.User;
import com.example.smoking.platform.model.UserRole;
import com.example.smoking.platform.service.RatingService;
import com.example.smoking.platform.service.SmokingLogService;
import com.example.smoking.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/api")
public class AdminReportController {

    @Autowired
    private SmokingLogService smokingLogService;

    @Autowired
    private UserService userService;

    @Autowired
    private RatingService ratingService;

    @GetMapping("/weekly-smoking-stats")
    public Map<String, Object> getWeeklySmokingStats() {
        Map<String, Object> result = new HashMap<>();
        List<User> members = userService.getAllUsers().stream()
                .filter(u -> u.getRole() == UserRole.MEMBER)
                .collect(Collectors.toList());

        // Tính trung bình số điếu mỗi tuần
        Map<String, Integer> totalByWeek = new LinkedHashMap<>();
        for (User user : members) {
            Map<String, Integer> userStats = smokingLogService.getSmokingStatsByWeek(user);
            for (Map.Entry<String, Integer> entry : userStats.entrySet()) {
                totalByWeek.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        result.put("labels", totalByWeek.keySet());
        result.put("data", totalByWeek.values());
        result.put("title", "Số điếu hút theo tuần (Toàn hệ thống)");

        return result;
    }

    @GetMapping("/money-saved-stats")
    public Map<String, Object> getMoneySavedStats(@RequestParam(defaultValue = "30") int days) {
        Map<LocalDate, Double> totalMap = new TreeMap<>();

        List<User> members = userService.getAllUsers().stream()
                .filter(u -> u.getRole() == UserRole.MEMBER)
                .collect(Collectors.toList());

        for (User user : members) {
            Map<LocalDate, Double> userData = smokingLogService.getCumulativeMoneySavedChartData(user, days);
            for (Map.Entry<LocalDate, Double> entry : userData.entrySet()) {
                totalMap.merge(entry.getKey(), entry.getValue(), Double::sum);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", totalMap.keySet().stream().map(LocalDate::toString).toList());
        result.put("data", totalMap.values());
        result.put("title", "Tổng tiền tiết kiệm tích lũy");

        return result;
    }

    @GetMapping("/ratings-summary")
    public Map<String, Object> getRatingSummary() {
        Map<String, Object> result = new HashMap<>();
        result.put("averageStars", ratingService.calculateAverageStars());
        result.put("totalRatings", ratingService.getAllRatings().size());
        return result;
    }

    @GetMapping("/user-role-distribution")
    public Map<String, Object> getUserRoleDistribution() {
        Map<UserRole, Long> countMap = userService.getAllUsers().stream()
                .collect(Collectors.groupingBy(User::getRole, Collectors.counting()));

        Map<String, Object> result = new HashMap<>();
        result.put("labels", countMap.keySet().stream().map(Enum::name).toList());
        result.put("data", countMap.values());
        return result;
    }
}
