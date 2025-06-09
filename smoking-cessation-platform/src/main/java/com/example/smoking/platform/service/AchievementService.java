package com.example.smoking.platform.service;

import com.example.smoking.platform.model.Achievement;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.model.UserAchievement;
import com.example.smoking.platform.repository.AchievementRepository;
import com.example.smoking.platform.repository.UserAchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new IllegalArgumentException("Huy hiệu với tên '" + achievement.getName() + "' đã tồn tại.");
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

            List<UserAchievement> userAchievementsToDelete = userAchievementRepository.findByAchievement(achievement);
            userAchievementRepository.deleteAll(userAchievementsToDelete);

            achievementRepository.delete(achievement);
        } else {
            throw new IllegalArgumentException("Không tìm thấy huy hiệu với ID: " + id);
        }
    }

    // --- Gán/Kiểm tra Huy hiệu cho Người dùng ---

    // Gán huy hiệu cho người dùng bằng ID
    @Transactional // Quan trọng để thao tác database
    public String grantAchievementToUser(User user, Achievement achievement) {
        // Kiểm tra xem người dùng đã có huy hiệu này chưa
        if (userAchievementRepository.findByUserAndAchievement(user, achievement).isPresent()) {
            return "Người dùng '" + user.getUsername() + "' đã đạt huy hiệu '" + achievement.getName() + "' rồi.";
        }

        // Tạo UserAchievement mới
        UserAchievement userAchievement = new UserAchievement();
        userAchievement.setUser(user);
        userAchievement.setAchievement(achievement);
        userAchievement.setDateAchieved(LocalDateTime.now()); // Ngày gán huy hiệu

        userAchievementRepository.save(userAchievement); // Lưu vào database
        return null; // Trả về null nếu thành công
    }


    // Lấy tất cả huy hiệu của một người dùng
    public List<UserAchievement> getUserAchievements(User user) {
        return userAchievementRepository.findByUser(user);
    }

    // Thêm phương thức này vào UserRepository.java để xóa UserAchievement theo Achievement
    // public void deleteByAchievement(Achievement achievement); // Cần thêm vào UserAchievementRepository
}