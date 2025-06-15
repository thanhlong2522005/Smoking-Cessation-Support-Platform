package com.example.smoking.platform.service;

import com.example.smoking.platform.model.SmokingLog;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.repository.SmokingLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Giữ lại nếu bạn sử dụng

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters; // Thêm import này
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
        if (smokingLog.getCigarettesSmoked() < 0) { // Sửa thành < 0, vì có thể log 0 điếu khi cố cai
            throw new IllegalArgumentException("Số điếu không được âm");
        }
        // Cho phép costPerCigarette là 0 nếu người dùng không điền hoặc không quan tâm
        // Tuy nhiên, nếu nó được dùng để tính tiền tiết kiệm, bạn cần đảm bảo nó > 0
        if (smokingLog.getCigarettesSmoked() > 0 && smokingLog.getCostPerCigarette() <= 0) {
             throw new IllegalArgumentException("Giá tiền phải lớn hơn 0 khi số điếu lớn hơn 0");
        }
        if (smokingLog.getFrequency() == null) {
            throw new IllegalArgumentException("Tần suất là bắt buộc");
        }

        // Đặt thời gian hiện tại nếu chưa có
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
        // Sử dụng phương thức sắp xếp trong repository
        return smokingLogRepository.findByUserOrderByDateDesc(user);
    }

    public List<SmokingLog> getLogsByUserAndDate(User user, LocalDateTime start, LocalDateTime end) {
        if (user == null || start == null || end == null || start.isAfter(end)) {
            throw new IllegalArgumentException("Thông tin thời gian hoặc user không hợp lệ");
        }
        // Sử dụng phương thức sắp xếp trong repository
        return smokingLogRepository.findByUserAndDateBetweenOrderByDateDesc(user, start, end);
    }

    /**
     * Tính số ngày không hút thuốc. Dựa vào `quitStartDate` của User và nhật ký hút thuốc gần nhất.
     * Nếu có log hút thuốc > 0 điếu sau `quitStartDate`, thì ngày bắt đầu tính không hút thuốc là sau ngày log đó.
     */
    public int calculateDaysWithoutSmoking(User user) {
        if (user == null || user.getQuitStartDate() == null) {
            return 0; // Không có thông tin để tính
        }

        LocalDate quitStartDate = user.getQuitStartDate();
        LocalDate today = LocalDate.now();

        // Tìm ngày cuối cùng người dùng ghi nhận hút thuốc (có số điếu > 0)
        LocalDateTime lastSmokingLogDate = smokingLogRepository.findLastSmokingDateByUser(user);
        LocalDate lastSmokeDateWithCigarettes = (lastSmokingLogDate != null) ? lastSmokingLogDate.toLocalDate() : null;

        LocalDate effectiveQuitDate;

        if (lastSmokeDateWithCigarettes != null && lastSmokeDateWithCigarettes.isAfter(quitStartDate.minusDays(1))) { // So sánh với ngày trước ngày cai để bao gồm ngày cai
            effectiveQuitDate = lastSmokeDateWithCigarettes.plusDays(1); // Ngày bắt đầu không hút là sau ngày cuối cùng hút thuốc
        } else {
            effectiveQuitDate = quitStartDate; // Nếu không có log hút thuốc sau ngày cai, tính từ ngày cai đã đặt
        }

        if (effectiveQuitDate.isAfter(today)) {
            return 0; // Ngày bắt đầu cai hiệu quả là trong tương lai, hoặc hôm nay chưa hết ngày
        }

        return (int) ChronoUnit.DAYS.between(effectiveQuitDate, today);
    }

    /**
     * Tính tổng chi phí đã bỏ ra cho việc hút thuốc dựa trên các log đã ghi nhận.
     */
    public double calculateTotalCost(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User không được null");
        }
        // Lấy tất cả các log của người dùng và tính tổng chi phí
        List<SmokingLog> logs = smokingLogRepository.findByUser(user);
        return logs.stream()
                .mapToDouble(log -> log.getCigarettesSmoked() * log.getCostPerCigarette())
                .sum();
    }

    /**
     * Tính tổng số tiền tiềm năng đã tiết kiệm (tiền đáng lẽ sẽ chi nếu không cai).
     * Hàm này tương tự `calculatePotentialMoneySaved` nhưng trả về `double` thay vì `Optional<Double>`.
     */
    public double calculateMoneySaved(User user) {
        if (user.getCigarettesPerDay() == null || user.getCigarettesPerDay() <= 0 ||
            user.getCostPerPack() == null || user.getCostPerPack() <= 0 ||
            user.getQuitStartDate() == null) {
            return 0.0; // Thiếu dữ liệu để tính toán
        }

        // Số ngày không hút thuốc
        long smokeFreeDays = calculateDaysWithoutSmoking(user);
        if (smokeFreeDays <= 0) { // Nếu không có ngày không hút nào hoặc ngày cai chưa đến
            return 0.0;
        }

        // Giả sử 20 điếu thuốc trong một gói để tính toán
        double costPerCigaretteBeforeQuit = user.getCostPerPack() / 20.0;
        double dailyCostBeforeQuit = costPerCigaretteBeforeQuit * user.getCigarettesPerDay();

        // Tiền tiết kiệm tiềm năng là số ngày không hút * chi phí hàng ngày trước khi cai
        return dailyCostBeforeQuit * smokeFreeDays;
    }


    public Map<String, Integer> getSmokingStatsByWeek(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User không được null");
        }
        LocalDateTime now = LocalDateTime.now();
        // Lấy ngày đầu tuần hiện tại (ví dụ: Thứ Hai)
        LocalDateTime currentWeekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate().atStartOfDay();
        LocalDateTime fourWeeksAgoStart = currentWeekStart.minusWeeks(3); // Bắt đầu từ 4 tuần trước (bao gồm cả tuần hiện tại)

        // Lấy logs trong 4 tuần gần nhất
        List<SmokingLog> logs = smokingLogRepository.findByUserAndDateBetweenOrderByDateDesc(user, fourWeeksAgoStart, now);

        Map<String, Integer> stats = new TreeMap<>();
        for (int i = 0; i < 4; i++) {
            LocalDateTime weekStart = currentWeekStart.minusWeeks(3 - i); // Tuần 1, Tuần 2, Tuần 3, Tuần 4
            LocalDateTime weekEnd = weekStart.plusWeeks(1);
            String weekLabel = "Tuần " + (i + 1); // Đặt nhãn tuần từ 1 đến 4

            int cigarettes = logs.stream()
                    .filter(log -> !log.getDate().isBefore(weekStart) && log.getDate().isBefore(weekEnd))
                    .mapToInt(SmokingLog::getCigarettesSmoked)
                    .sum();
            stats.put(weekLabel, cigarettes);
        }
        return stats;
    }

    public String estimateHealthImprovement(User user) {
        int daysWithoutSmoking = calculateDaysWithoutSmoking(user);
        if (daysWithoutSmoking >= 365) {
            return "Tuyệt vời! Nguy cơ bệnh tim và đột quỵ giảm đáng kể, phổi hồi phục gần như hoàn toàn.";
        } else if (daysWithoutSmoking >= 90) {
            return "Cải thiện lớn! Chức năng phổi tăng lên, giảm nguy cơ nhiễm trùng.";
        } else if (daysWithoutSmoking >= 30) {
            return "Sức khỏe cải thiện đáng kể: Hệ hô hấp tốt hơn, giảm nguy cơ bệnh tim.";
        } else if (daysWithoutSmoking >= 7) {
            return "Sức khỏe bắt đầu cải thiện: Huyết áp ổn định hơn, giác quan phục hồi.";
        } else if (daysWithoutSmoking > 0) {
            return "Chúc mừng bạn đã bắt đầu hành trình cai thuốc! Cơ thể đang dần hồi phục.";
        } else {
            return "Tiếp tục cố gắng! Hãy đặt mục tiêu và ghi lại nhật ký đều đặn để thấy sự thay đổi.";
        }
    }

    // --- Các phương thức hỗ trợ Dashboard và Biểu đồ ---

    /**
     * Tìm nhật ký hút thuốc gần đây nhất của một người dùng.
     *
     * @param user Đối tượng người dùng
     * @return Optional chứa SmokingLog gần nhất, hoặc rỗng nếu không có nhật ký nào.
     */
    public Optional<SmokingLog> findLatestSmokingLogByUser(User user) {
        // Sử dụng phương thức đã được sắp xếp trong repository
        return smokingLogRepository.findTopByUserOrderByDateDesc(user);
    }

    /**
     * Phương thức này là bản gốc của `getSmokeFreeDays` và có vẻ logic của `calculateDaysWithoutSmoking` đã thay thế nó.
     * Cân nhắc xóa hoặc hợp nhất để tránh trùng lặp.
     * Tôi sẽ tạm thời giữ lại để bạn xem xét.
     */
    public Optional<Long> getSmokeFreeDays(User user) {
        // Logic này đã được xử lý chi tiết hơn trong calculateDaysWithoutSmoking
        // Nếu bạn muốn một Optional, có thể gọi calculateDaysWithoutSmoking và bọc nó.
        int days = calculateDaysWithoutSmoking(user);
        return Optional.of((long) days);
    }

    /**
     * Tính toán số tiền tiềm năng đã tiết kiệm dựa trên số ngày không hút thuốc.
     * Phương thức này là bản gốc của `calculateMoneySaved` và có vẻ logic của `calculateMoneySaved` đã thay thế nó.
     * Cân nhắc xóa hoặc hợp nhất để tránh trùng lặp.
     * Tôi sẽ tạm thời giữ lại để bạn xem xét.
     */
    public Optional<Double> calculatePotentialMoneySaved(User user) {
        // Logic này đã được xử lý chi tiết hơn trong calculateMoneySaved
        // Nếu bạn muốn một Optional, có thể gọi calculateMoneySaved và bọc nó.
        double saved = calculateMoneySaved(user);
        return Optional.of(saved);
    }

    /**
     * Tính toán số tiền tiết kiệm tích lũy theo từng ngày từ ngày bắt đầu cai thuốc.
     * Đây là hàm mới để lấy dữ liệu cho biểu đồ đường.
     *
     * @param user Người dùng hiện tại.
     * @param daysToLookBack Số ngày muốn thống kê trở lại từ hiện tại (ví dụ: 90 ngày).
     * @return Map với LocalDate là khóa (ngày) và Double là tổng tiền tiết kiệm tích lũy đến ngày đó.
     */
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
}