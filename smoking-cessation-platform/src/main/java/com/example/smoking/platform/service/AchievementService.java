package com.example.smoking.platform.service;

import com.example.smoking.platform.model.Achievement;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.model.UserAchievement;
import com.example.smoking.platform.repository.AchievementRepository;
import com.example.smoking.platform.repository.UserAchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserAchievementRepository userAchievementRepository;

    // --- Quản lý các loại Huy hiệu (dành cho Admin) ---

    // Tạo huy hiệu mới
    public Achievement createAchievement(Achievement achievement) {
        if (achievementRepository.findByName(achievement.getName()).isPresent()) {
            // Không ném lỗi nếu huy hiệu đã tồn tại, chỉ trả về huy hiệu đó
            return achievementRepository.findByName(achievement.getName()).get();
        }
        return achievementRepository.save(achievement);
    }

    // Lấy tất cả các loại huy hiệu
    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    // Lấy huy hiệu theo ID
    public Optional<Achievement> getAchievementById(Long id) {
        return achievementRepository.findById(id);
    }

    // Cập nhật huy hiệu
    public Achievement updateAchievement(Achievement achievement) {
        if (!achievementRepository.existsById(achievement.getId())) {
            throw new IllegalArgumentException("Huy hiệu với ID " + achievement.getId() + " không tồn tại.");
        }
        // Kiểm tra tên trùng lặp nếu tên được thay đổi và đã tồn tại ở huy hiệu khác
        Optional<Achievement> existingAchievement = achievementRepository.findByName(achievement.getName());
        if (existingAchievement.isPresent() && !existingAchievement.get().getId().equals(achievement.getId())) {
            throw new IllegalArgumentException("Huy hiệu với tên '" + achievement.getName() + "' đã tồn tại.");
        }
        return achievementRepository.save(achievement);
    }

    // Xóa huy hiệu
    @Transactional
    public void deleteAchievement(Long id) {
        Optional<Achievement> achievementOptional = achievementRepository.findById(id);
        if (achievementOptional.isPresent()) {
            Achievement achievement = achievementOptional.get();
            // Trước khi xóa huy hiệu, xóa tất cả UserAchievement liên quan
            userAchievementRepository.deleteByAchievement(achievement);
            achievementRepository.delete(achievement);
        } else {
            throw new IllegalArgumentException("Không tìm thấy huy hiệu với ID: " + id);
        }
    }

    // --- Gán/Kiểm tra Huy hiệu cho Người dùng (tự động hoặc thủ công) ---

    /**
     * Gán huy hiệu cho người dùng.
     * Trả về true nếu huy hiệu được gán thành công, false nếu người dùng đã có huy hiệu này.
     * @param user Người dùng
     * @param achievement Huy hiệu
     * @return true nếu gán thành công, false nếu đã có
     */
    @Transactional
    public String grantAchievementToUser(User user, Achievement achievement) {
    if (user == null || achievement == null) {
        return "Thông tin người dùng hoặc huy hiệu không hợp lệ.";
    }

    if (userAchievementRepository.findByUserAndAchievement(user, achievement).isPresent()) {
        return "Người dùng đã có huy hiệu này.";
    }

        // Tạo UserAchievement mới
        UserAchievement userAchievement = new UserAchievement();
        userAchievement.setUser(user);
        userAchievement.setAchievement(achievement);
        userAchievement.setDateAchieved(LocalDateTime.now()); // Ngày gán huy hiệu

        userAchievementRepository.save(userAchievement); // Lưu vào database
        return null; // Gán thành công
    }

    /**
     * Thu hồi huy hiệu khỏi người dùng.
     * @param user Người dùng
     * @param achievement Huy hiệu
     * @return true nếu thu hồi thành công, false nếu người dùng không có huy hiệu này
     */
    @Transactional
    public boolean revokeAchievementFromUser(User user, Achievement achievement) {
        Optional<UserAchievement> userAchievementOptional = userAchievementRepository.findByUserAndAchievement(user, achievement);
        if (userAchievementOptional.isPresent()) {
            userAchievementRepository.delete(userAchievementOptional.get());
            return true;
        }
        return false; // Người dùng không có huy hiệu này
    }


    // Lấy tất cả huy hiệu của một người dùng
    public List<UserAchievement> getUserAchievements(User user) {
        return userAchievementRepository.findByUserOrderByDateAchievedAsc(user);
    }

    // Phương thức kiểm tra và cập nhật tất cả huy hiệu tự động cho một người dùng
    @Transactional
    public void evaluateAndAwardAchievements(User user) {
        // 1. Kiểm tra và cấp/thu hồi huy hiệu thời gian
        evaluateTimeBasedAchievements(user);

        // 2. Kiểm tra và cấp/thu hồi huy hiệu tiết kiệm tiền
        evaluateMoneyBasedAchievements(user);

        // 3. Kiểm tra và cấp/thu hồi huy hiệu hoàn thành mục tiêu (nếu có logic cụ thể)
        evaluateGoalBasedAchievement(user);
    }

    // Helper method để kiểm tra và gán/thu hồi huy hiệu thời gian
    private void evaluateTimeBasedAchievements(User user) {
        Optional<Long> smokeFreeDaysOptional = smokingLogService.getSmokeFreeDays(user); // Cần inject SmokingLogService
        long smokeFreeDays = smokeFreeDaysOptional.orElse(0L);

        // Danh sách các huy hiệu thời gian và số ngày tương ứng
        List<AchievementThreshold> timeAchievements = List.of(
            new AchievementThreshold("1-Day Free", 1),
            new AchievementThreshold("1-Week Free", 7),
            new AchievementThreshold("1-Month Free", 30),
            new AchievementThreshold("3-Months Free", 90),
            new AchievementThreshold("6-Months Free", 180),
            new AchievementThreshold("1-Year Free", 365)
        );

        for (AchievementThreshold threshold : timeAchievements) {
            Optional<Achievement> achievementOptional = achievementRepository.findByName(threshold.name);
            if (achievementOptional.isPresent()) {
                Achievement achievement = achievementOptional.get();
                if (smokeFreeDays >= threshold.days) {
                    grantAchievementToUser(user, achievement); // Thử gán, nếu đã có thì không làm gì
                } else {
                    revokeAchievementFromUser(user, achievement); // Nếu không đạt điều kiện nữa thì thu hồi
                }
            }
        }
    }

    // Helper method để kiểm tra và gán/thu hồi huy hiệu tiết kiệm tiền
    private void evaluateMoneyBasedAchievements(User user) {
        Optional<Double> potentialMoneySavedOptional = smokingLogService.calculatePotentialMoneySaved(user); // Cần inject SmokingLogService
        double moneySaved = potentialMoneySavedOptional.orElse(0.0);

        List<AchievementMoneyThreshold> moneyAchievements = List.of(
            new AchievementMoneyThreshold("Money Saver I", 100000.0),
            new AchievementMoneyThreshold("Money Saver II", 500000.0),
            new AchievementMoneyThreshold("Money Saver III", 1000000.0)
        );

        for (AchievementMoneyThreshold threshold : moneyAchievements) {
            Optional<Achievement> achievementOptional = achievementRepository.findByName(threshold.name);
            if (achievementOptional.isPresent()) {
                Achievement achievement = achievementOptional.get();
                if (moneySaved >= threshold.amount) {
                    grantAchievementToUser(user, achievement);
                } else {
                    revokeAchievementFromUser(user, achievement);
                }
            }
        }
    }

    // Helper method để kiểm tra và gán/thu hồi huy hiệu hoàn thành mục tiêu
    private void evaluateGoalBasedAchievement(User user) {
        // Điều kiện để đạt huy hiệu "Quit Goal Achieved":
        // 1. Người dùng đã đặt expectedQuitDate
        // 2. Ngày hiện tại >= expectedQuitDate
        // 3. Người dùng đã duy trì không hút thuốc (smokeFreeDays >= số ngày dự kiến từ quitStartDate đến expectedQuitDate)
        // Đây là một ví dụ, bạn có thể định nghĩa logic phức tạp hơn.

        Optional<Achievement> achievementOptional = achievementRepository.findByName("Quit Goal Achieved");
        if (achievementOptional.isEmpty()) {
            return; // Không có huy hiệu này, thoát
        }
        Achievement quitGoalAchievement = achievementOptional.get();

        if (user.getExpectedQuitDate() != null && user.getQuitStartDate() != null) {
            LocalDate today = LocalDate.now();
            if (today.isAfter(user.getExpectedQuitDate()) || today.isEqual(user.getExpectedQuitDate())) {
                // Đã đến hoặc qua ngày dự kiến bỏ thuốc
                // Kiểm tra xem họ có thực sự không hút thuốc từ đó đến giờ không
                Optional<Long> smokeFreeDays = smokingLogService.getSmokeFreeDays(user);
                if (smokeFreeDays.isPresent()) {
                    // Tính số ngày dự kiến từ quitStartDate đến expectedQuitDate
                    long expectedDaysToQuit = java.time.temporal.ChronoUnit.DAYS.between(user.getQuitStartDate(), user.getExpectedQuitDate()) + 1;
                    if (smokeFreeDays.get() >= expectedDaysToQuit) {
                        grantAchievementToUser(user, quitGoalAchievement);
                    } else {
                        revokeAchievementFromUser(user, quitGoalAchievement); // Nếu tái nghiện thì thu hồi
                    }
                }
            }
        } else {
            // Nếu không có thông tin mục tiêu, đảm bảo huy hiệu này không được cấp
            revokeAchievementFromUser(user, quitGoalAchievement);
        }
    }


    // Cần inject SmokingLogService để sử dụng các phương thức tính toán
    @Autowired
    private SmokingLogService smokingLogService;

    // Lớp nội bộ để dễ dàng quản lý ngưỡng huy hiệu thời gian
    private static class AchievementThreshold {
        String name;
        long days;

        public AchievementThreshold(String name, long days) {
            this.name = name;
            this.days = days;
        }
    }

    // Lớp nội bộ để dễ dàng quản lý ngưỡng huy hiệu tiền
    private static class AchievementMoneyThreshold {
        String name;
        double amount;

        public AchievementMoneyThreshold(String name, double amount) {
            this.name = name;
            this.amount = amount;
        }
    }
}