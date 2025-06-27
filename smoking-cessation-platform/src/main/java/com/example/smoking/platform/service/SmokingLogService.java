package com.example.smoking.platform.service;

import com.example.smoking.platform.model.SmokingLog;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.repository.SmokingLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class SmokingLogService {

    @Autowired
    private SmokingLogRepository smokingLogRepository;

    // Các phương thức hiện có của bạn...

    public SmokingLog save(SmokingLog smokingLog) {
        return smokingLogRepository.save(smokingLog);
    }

    public Optional<SmokingLog> findLatestSmokingLogByUser(User user) {
        return smokingLogRepository.findTopByUserOrderByDateDesc(user);
    }

    public int calculateDaysWithoutSmoking(User user) {
        if (user.getQuitStartDate() == null) {
            return 0; // No quit start date set
        }

        LocalDateTime quitStartDateTime = user.getQuitStartDate().atStartOfDay();

        Optional<SmokingLog> latestActualSmokingLog = smokingLogRepository
                .findFirstByUserAndCigarettesSmokedGreaterThanOrderByDateDesc(user, 0);

        LocalDateTime effectiveStartDate;

        if (latestActualSmokingLog.isPresent() && latestActualSmokingLog.get().getDate().isAfter(quitStartDateTime)) {
            effectiveStartDate = latestActualSmokingLog.get().getDate().toLocalDate().plusDays(1).atStartOfDay();
        } else {
            effectiveStartDate = quitStartDateTime;
        }

        if (effectiveStartDate.isAfter(LocalDateTime.now())) {
            return 0;
        }

        long days = ChronoUnit.DAYS.between(effectiveStartDate.toLocalDate(), LocalDate.now());
        return (int) Math.max(0, days);
    }

    public double calculateMoneySaved(User user) {
        if (user.getCigarettesPerDay() == null || user.getCigarettesPerDay() <= 0 ||
                user.getCostPerPack() == null || user.getCostPerPack() <= 0 ||
                user.getQuitStartDate() == null || user.getCigarettesPerPack() == null || user.getCigarettesPerPack() <= 0) {
            return 0.0;
        }

        int smokeFreeDays = calculateDaysWithoutSmoking(user);

        if (smokeFreeDays <= 0) {
            return 0.0;
        }

        double costPerCigaretteBeforeQuit = user.getCostPerPack() / user.getCigarettesPerPack();
        double dailyCostBeforeQuit = costPerCigaretteBeforeQuit * user.getCigarettesPerDay();

        return dailyCostBeforeQuit * smokeFreeDays;
    }

    public Map<String, Integer> getSmokingStatsByWeek(User user) {
        LocalDateTime sevenWeeksAgo = LocalDateTime.now().minusWeeks(7);
        List<SmokingLog> logs = smokingLogRepository.findByUserAndDateAfterOrderByDateAsc(user, sevenWeeksAgo);

        Map<String, Integer> weeklyStats = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate weekStart = today.minusWeeks(i).with(java.time.DayOfWeek.MONDAY);
            weeklyStats.put("Tuần " + weekStart.format(DateTimeFormatter.ofPattern("dd/MM")), 0);
        }

        for (SmokingLog log : logs) {
            LocalDate logDate = log.getDate().toLocalDate();
            LocalDate weekStart = logDate.with(java.time.DayOfWeek.MONDAY);
            String weekLabel = "Tuần " + weekStart.format(DateTimeFormatter.ofPattern("dd/MM"));
            weeklyStats.merge(weekLabel, log.getCigarettesSmoked(), Integer::sum);
        }
        return weeklyStats;
    }

    public Map<LocalDate, Double> getCumulativeMoneySavedChartData(User user, int daysToLookBack) {
        if (user == null || user.getQuitStartDate() == null ||
            user.getCigarettesPerDay() == null || user.getCigarettesPerDay() <= 0 ||
            user.getCostPerPack() == null || user.getCostPerPack() <= 0) {
            return new TreeMap<>(); // Trả về map rỗng nếu thiếu thông tin
        }

        LocalDate quitStartDate = user.getQuitStartDate();
        LocalDate today = LocalDate.now();

        // Đảm bảo không nhìn quá khứ hơn quitStartDate
        LocalDate chartStartDate = today.minusDays(daysToLookBack - 1);
        if (chartStartDate.isBefore(quitStartDate)) {
            chartStartDate = quitStartDate; // Bắt đầu từ ngày cai hoặc ngày sớm hơn đã yêu cầu
        }

        double costPerCigaretteBeforeQuit = user.getCostPerPack() / 20.0; // Giả sử 20 điếu/gói
        double dailyCostBeforeQuit = costPerCigaretteBeforeQuit * user.getCigarettesPerDay();

        Map<LocalDate, Double> cumulativeSavings = new TreeMap<>();
        double currentCumulativeSaved = 0.0;

        // Lấy tất cả các nhật ký hút thuốc của người dùng sau ngày bắt đầu xem xét
        // Sử dụng findByUserAndDateAfterOrderByDateAsc để đảm bảo thứ tự
        List<SmokingLog> logs = smokingLogRepository.findByUserAndDateAfterOrderByDateAsc(user, chartStartDate.atStartOfDay());

        // Tạo một map để dễ dàng tra cứu số điếu hút mỗi ngày
        Map<LocalDate, Integer> actualSmokedByDate = logs.stream()
            .collect(Collectors.groupingBy(log -> log.getDate().toLocalDate(),
                                            Collectors.summingInt(SmokingLog::getCigarettesSmoked)));

        // Duyệt từng ngày từ chartStartDate đến hôm nay để tính tích lũy
        for (LocalDate date = chartStartDate; !date.isAfter(today); date = date.plusDays(1)) {
            // Số điếu đáng lẽ sẽ hút nếu không cai
            double expectedCigarettes = user.getCigarettesPerDay();

            // Số điếu thực tế đã hút vào ngày này (nếu có log)
            int actualSmoked = actualSmokedByDate.getOrDefault(date, 0);

            // Số điếu không hút trong ngày này
            double cigarettesNotSmokedThisDay = expectedCigarettes - actualSmoked;

            // Nếu số điếu không hút > 0, tính tiền tiết kiệm cho ngày này
            if (cigarettesNotSmokedThisDay > 0) {
                currentCumulativeSaved += cigarettesNotSmokedThisDay * costPerCigaretteBeforeQuit;
            }
            // Nếu actualSmoked >= expectedCigarettes, coi như không tiết kiệm thêm trong ngày đó.
            // Logic này có thể được điều chỉnh nếu bạn muốn tiền tiết kiệm giảm khi tái nghiện.

            cumulativeSavings.put(date, currentCumulativeSaved);
        }

        return cumulativeSavings;
    }


    public List<HealthMilestone> getHealthProgress(User user) {
        List<HealthMilestone> milestones = new ArrayList<>();

        Optional<SmokingLog> latestActualSmokingLog = smokingLogRepository
                .findFirstByUserAndCigarettesSmokedGreaterThanOrderByDateDesc(user, 0);

        LocalDateTime lastCigaretteTime;

        if (latestActualSmokingLog.isPresent() && user.getQuitStartDate() != null && latestActualSmokingLog.get().getDate().isAfter(user.getQuitStartDate().atStartOfDay())) {
            lastCigaretteTime = latestActualSmokingLog.get().getDate();
        } else if (user.getQuitStartDate() != null) {
            lastCigaretteTime = user.getQuitStartDate().atStartOfDay();
        } else {
            return Collections.emptyList();
        }

        LocalDateTime now = LocalDateTime.now();
        Duration durationSinceLastCigarette = Duration.between(lastCigaretteTime, now);

        if (durationSinceLastCigarette.isNegative()) {
            return Collections.emptyList();
        }

        long minutes = durationSinceLastCigarette.toMinutes();
        long hours = durationSinceLastCigarette.toHours();
        long days = durationSinceLastCigarette.toDays();

        // Milestone 1: 20 minutes smoke-free
        if (minutes >= 20) {
            milestones.add(new HealthMilestone("Huyết áp & nhịp tim giảm", "Huyết áp và nhịp tim bắt đầu trở lại bình thường.", true));
        } else {
            milestones.add(new HealthMilestone("Huyết áp & nhịp tim giảm (20 phút)", "Sau 20 phút, huyết áp và nhịp tim của bạn sẽ giảm trở lại bình thường.", false));
        }

        // Milestone 2: 8 hours smoke-free
        if (hours >= 8) {
            milestones.add(new HealthMilestone("Carbon monoxide giảm", "Nồng độ carbon monoxide trong máu giảm xuống mức bình thường.", true));
        } else {
            milestones.add(new HealthMilestone("Carbon monoxide giảm (8 giờ)", "Sau 8 giờ, nồng độ carbon monoxide trong máu giảm xuống mức bình thường.", false));
        }

        // Milestone 3: 24 hours (1 day) smoke-free
        if (hours >= 24) {
            milestones.add(new HealthMilestone("Nguy cơ đau tim giảm", "Nguy cơ đau tim của bạn bắt đầu giảm.", true));
        } else {
            milestones.add(new HealthMilestone("Nguy cơ đau tim giảm (24 giờ)", "Sau 24 giờ, nguy cơ đau tim của bạn bắt đầu giảm.", false));
        }

        // Milestone 4: 48 hours (2 days) smoke-free
        if (hours >= 48) {
            milestones.add(new HealthMilestone("Cải thiện vị giác & khứu giác", "Các đầu dây thần kinh bị tổn thương bắt đầu hồi phục, vị giác và khứu giác của bạn được cải thiện.", true));
        } else {
            milestones.add(new HealthMilestone("Cải thiện vị giác & khứu giác (48 giờ)", "Sau 48 giờ, vị giác và khứu giác của bạn sẽ được cải thiện.", false));
        }

        // Milestone 5: 2-12 weeks smoke-free
        if (days >= 14 && days <= 84) { // 2 weeks to 12 weeks
             milestones.add(new HealthMilestone("Chức năng phổi & lưu thông máu cải thiện", "Chức năng phổi được cải thiện đáng kể và lưu thông máu tốt hơn.", true));
        } else if (days > 0) { // If quit for > 0 days
            milestones.add(new HealthMilestone("Chức năng phổi & lưu thông máu (2-12 tuần)", "Trong 2 đến 12 tuần, chức năng phổi và lưu thông máu sẽ cải thiện.", false));
        }

        // Milestone 6: 1 year smoke-free
        if (days >= 365) {
            milestones.add(new HealthMilestone("Nguy cơ bệnh tim giảm một nửa", "Nguy cơ mắc bệnh tim mạch vành giảm đi một nửa so với người hút thuốc.", true));
        } else if (days > 0) {
            milestones.add(new HealthMilestone("Nguy cơ bệnh tim giảm (1 năm)", "Sau 1 năm, nguy cơ mắc bệnh tim của bạn sẽ giảm một nửa.", false));
        }

        // Milestone 7: 5 years smoke-free
        if (days >= 5 * 365) {
            milestones.add(new HealthMilestone("Nguy cơ đột quỵ giảm", "Nguy cơ đột quỵ trở lại mức của người không hút thuốc.", true));
        } else if (days > 0) {
            milestones.add(new HealthMilestone("Nguy cơ đột quỵ giảm (5 năm)", "Sau 5 năm, nguy cơ đột quỵ của bạn sẽ giảm đáng kể.", false));
        }

        // Milestone 8: 10 years smoke-free
        if (days >= 10 * 365) {
            milestones.add(new HealthMilestone("Nguy cơ ung thư phổi giảm", "Nguy cơ tử vong do ung thư phổi giảm một nửa so với người hút thuốc.", true));
        } else if (days > 0) {
            milestones.add(new HealthMilestone("Nguy cơ ung thư phổi giảm (10 năm)", "Sau 10 năm, nguy cơ ung thư phổi của bạn sẽ giảm đáng kể.", false));
        }

        // Milestone 9: 15 years smoke-free
        if (days >= 15 * 365) {
            milestones.add(new HealthMilestone("Nguy cơ bệnh tim & tử vong ngang người không hút thuốc", "Nguy cơ mắc bệnh tim và tử vong trở lại mức của người chưa từng hút thuốc.", true));
        } else if (days > 0) {
            milestones.add(new HealthMilestone("Nguy cơ bệnh tim & tử vong (15 năm)", "Sau 15 năm, nguy cơ bệnh tim và tử vong của bạn sẽ ngang bằng người không hút thuốc.", false));
        }

        return milestones;
    }

    public SmokingLog saveSmokingLog(SmokingLog smokingLog) {
        return smokingLogRepository.save(smokingLog);
    }

    public List<SmokingLog> getLogsByUser(User user) {
        return smokingLogRepository.findByUser(user);
    }

    public List<SmokingLog> getLogsByUserAndDate(User user, LocalDateTime startDate, LocalDateTime endDate) {
        return smokingLogRepository.findByUserAndDateBetween(user, startDate, endDate);
    }

    public List<HealthMilestone> estimateHealthImprovement(User user) {
        return getHealthProgress(user);
    }

    public double calculateTotalCost(User user) {
        return calculateMoneySaved(user);
    }

    // --- CÁC PHƯƠNG THỨC MỚI ĐỂ KHẮC PHỤC LỖI TRONG AchievementService ---

    /**
     * Trả về số ngày không hút thuốc của người dùng.
     * Phương thức này gọi lại calculateDaysWithoutSmoking.
     *
     * @param user Người dùng
     * @return Optional chứa số ngày không hút thuốc, hoặc Optional.empty() nếu không tính được.
     */
    public Optional<Long> getSmokeFreeDays(User user) {
        return Optional.of((long) calculateDaysWithoutSmoking(user));
    }

    /**
     * Tính toán số tiền tiềm năng đã tiết kiệm được dựa trên thói quen hút thuốc trước đây của người dùng.
     * Phương thức này gọi lại calculateMoneySaved.
     *
     * @param user Người dùng
     * @return Optional chứa số tiền đã tiết kiệm, hoặc Optional.empty() nếu không tính được.
     */
    public Optional<Double> calculatePotentialMoneySaved(User user) {
        return Optional.of(calculateMoneySaved(user));
    }

    // Inner class for HealthMilestone (consider moving to its own file if used widely)
    public static class HealthMilestone {
        private String name;
        private String description;
        private boolean achieved;

        public HealthMilestone(String name, String description, boolean achieved) {
            this.name = name;
            this.description = description;
            this.achieved = achieved;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public boolean isAchieved() { return achieved; }
    }
}