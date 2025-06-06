package com.example.smoking.platform.service;
import com.example.smoking.platform.model.SmokingLog;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.repository.SmokingLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class SmokingLogService {

    @Autowired
    private SmokingLogRepository smokingLogRepository;

    public SmokingLog save(SmokingLog smokingLog) {
        // Kiểm tra dữ liệu đầu vào
        if (smokingLog.getUser() == null) {
            throw new IllegalArgumentException("User là bắt buộc");
        }
        if (smokingLog.getCigarettesSmoked() <= 0) {
            throw new IllegalArgumentException("Số điếu phải lớn hơn 0");
        }
        if (smokingLog.getCostPerCigarette() <= 0) {
            throw new IllegalArgumentException("Giá tiền phải lớn hơn 0");
        }
        if (smokingLog.getFrequency() == null) {
            throw new IllegalArgumentException("Tần suất là bắt buộc");
        }

        // Đặt thời gian hiện tại
        smokingLog.setDate(LocalDateTime.now()); // 03:28 PM +07, 06/06/2025
        return smokingLogRepository.save(smokingLog);
    }

    public List<SmokingLog> getLogsByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User không được null");
        }
        return smokingLogRepository.findByUser(user);
    }

    public List<SmokingLog> getLogsByUserAndDate(User user, LocalDateTime start, LocalDateTime end) {
        if (user == null || start == null || end == null || start.isAfter(end)) {
            throw new IllegalArgumentException("Thông tin thời gian hoặc user không hợp lệ");
        }
        return smokingLogRepository.findByUserAndDateBetween(user, start, end);
    }

    public int calculateDaysWithoutSmoking(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User không được null");
        }
        LocalDateTime lastSmoke = smokingLogRepository.findLastSmokingDateByUser(user);
        return lastSmoke != null ? (int) ChronoUnit.DAYS.between(lastSmoke, LocalDateTime.now()) : 0;
    }

    public double calculateTotalCost(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User không được null");
        }
        Integer totalCigarettes = smokingLogRepository.countTotalCigarettesByUser(user);
        double costPerCigarette = user.getCostPerPack() != null ? user.getCostPerPack() / 20 : 0.0; // Giả sử 1 gói có 20 điếu
        return totalCigarettes != null ? totalCigarettes * costPerCigarette : 0.0;
    }

    public Map<String, Integer> getSmokingStatsByWeek(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User không được null");
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fourWeeksAgo = now.minusWeeks(4);
        List<SmokingLog> logs = smokingLogRepository.findByUserAndDateBetween(user, fourWeeksAgo, now);

        Map<String, Integer> stats = new TreeMap<>();
        for (int i = 3; i >= 0; i--) {
            LocalDateTime weekStart = now.minusWeeks(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime weekEnd = weekStart.plusWeeks(1);
            String weekLabel = "Tuần " + (4 - i);
            int cigarettes = logs.stream()
                    .filter(log -> log.getDate().isAfter(weekStart) && log.getDate().isBefore(weekEnd))
                    .mapToInt(SmokingLog::getCigarettesSmoked)
                    .sum();
            stats.put(weekLabel, cigarettes);
        }
        return stats;
    }

    public String estimateHealthImprovement(User user) {
        int daysWithoutSmoking = calculateDaysWithoutSmoking(user);
        if (daysWithoutSmoking >= 30) {
            return "Sức khỏe cải thiện đáng kể: Hệ hô hấp tốt hơn, giảm nguy cơ bệnh tim.";
        } else if (daysWithoutSmoking >= 7) {
            return "Sức khỏe bắt đầu cải thiện: Huyết áp ổn định hơn.";
        } else {
            return "Tiếp tục cố gắng! Sức khỏe sẽ cải thiện sau vài ngày không hút.";
        }
    }
}