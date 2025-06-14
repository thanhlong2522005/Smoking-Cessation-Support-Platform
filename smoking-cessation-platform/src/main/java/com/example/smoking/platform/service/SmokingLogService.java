package com.example.smoking.platform.service;
import com.example.smoking.platform.model.SmokingLog;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.repository.SmokingLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class SmokingLogService {

    @Autowired
    private SmokingLogRepository smokingLogRepository;

    @Autowired
    @Lazy // Sử dụng @Lazy để tránh lỗi Circular Dependency
    private AchievementService achievementService;

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
        if (smokingLog.getDate() == null) {
            smokingLog.setDate(LocalDateTime.now());
        }

        SmokingLog savedLog = smokingLogRepository.save(smokingLog);

        // Sau khi lưu nhật ký, kiểm tra và cập nhật huy hiệu cho người dùng
        achievementService.evaluateAndAwardAchievements(savedLog.getUser());

        return savedLog;
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

    // --- Các phương thức mới cho Thông báo định kỳ ---

    /**
     * Tìm nhật ký hút thuốc gần đây nhất của một người dùng.
     *
     * @param user Đối tượng người dùng
     * @return Optional chứa SmokingLog gần nhất, hoặc rỗng nếu không có nhật ký nào.
     */
    public Optional<SmokingLog> findLatestSmokingLogByUser(User user) {
        return smokingLogRepository.findTopByUserOrderByDateDesc(user);
    }

    /**
     *
     * @param user Đối tượng người dùng
     * @return Optional chứa số ngày không hút thuốc, hoặc rỗng nếu người dùng chưa đặt ngày cai,
     * hoặc 0L nếu người dùng đã hút thuốc trở lại sau ngày cai.
     */
    public Optional<Long> getSmokeFreeDays(User user) {
        if (user == null || user.getQuitStartDate() == null) {
            return Optional.empty();
        }

        LocalDate quitStartDate = user.getQuitStartDate();
        LocalDate today = LocalDate.now();

        // Tìm ngày cuối cùng người dùng ghi nhận hút thuốc (từ log)
        // Nếu có log hút thuốc sau quitStartDate, thì ngày bắt đầu tính không hút thuốc sẽ là sau ngày đó.
        Optional<SmokingLog> latestSmokingLogOptional = smokingLogRepository.findTopByUserOrderByDateDesc(user);
        LocalDate lastSmokingDate = null;
        if (latestSmokingLogOptional.isPresent()) {
            SmokingLog latestLog = latestSmokingLogOptional.get();
            if (latestLog.getCigarettesSmoked() > 0) {
                lastSmokingDate = latestLog.getDate().toLocalDate();
            }
        }

        LocalDate effectiveQuitStartDate;
        if (lastSmokingDate != null && lastSmokingDate.isAfter(quitStartDate)) {
            // Nếu có log hút thuốc sau ngày bắt đầu cai, thì ngày bắt đầu tính không hút thuốc là sau ngày log đó
            effectiveQuitStartDate = lastSmokingDate.plusDays(1);
        } else {
            // Ngược lại, tính từ ngày bắt đầu cai đã đặt
            effectiveQuitStartDate = quitStartDate;
        }

        // Nếu ngày bắt đầu có hiệu lực lớn hơn ngày hiện tại, nghĩa là chưa đạt được ngày không hút thuốc nào.
        if (effectiveQuitStartDate.isAfter(today)) {
            return Optional.of(0L);
        }

        long daysBetween = ChronoUnit.DAYS.between(effectiveQuitStartDate, today);

        return Optional.of(daysBetween);
    }


    /**
     * Tính toán số tiền tiềm năng đã tiết kiệm dựa trên số ngày không hút thuốc.
     *
     * @param user Đối tượng người dùng
     * @return Optional chứa số tiền đã tiết kiệm, hoặc rỗng nếu thiếu dữ liệu tính toán.
     */
    public Optional<Double> calculatePotentialMoneySaved(User user) {
        if (user.getCigarettesPerDay() == null || user.getCigarettesPerDay() <= 0 ||
            user.getCostPerPack() == null || user.getCostPerPack() <= 0 ||
            user.getQuitStartDate() == null) {
            return Optional.empty(); // Thiếu dữ liệu để tính toán
        }

        Optional<Long> smokeFreeDaysOptional = getSmokeFreeDays(user);
        if (smokeFreeDaysOptional.isEmpty() || smokeFreeDaysOptional.get() <= 0) {
            return Optional.of(0.0); // Không có ngày hút thuốc hoặc ngày cai không được đặt đúng/tái nghiện
        }

        long smokeFreeDays = smokeFreeDaysOptional.get();
        if (smokeFreeDays == 0) return Optional.of(0.0); // Nếu họ tái nghiện ngay hôm nay

        // Giả sử 20 điếu thuốc trong một gói để tính toán
        double costPerCigaretteBeforeQuit = user.getCostPerPack() / 20.0;
        double dailyCostBeforeQuit = costPerCigaretteBeforeQuit * user.getCigarettesPerDay();

        double totalMoneySaved = dailyCostBeforeQuit * smokeFreeDays;
        return Optional.of(totalMoneySaved);
    }
}