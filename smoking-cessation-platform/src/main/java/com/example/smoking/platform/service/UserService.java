package com.example.smoking.platform.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List; // Import UserRole
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.smoking.platform.model.User;
import com.example.smoking.platform.model.UserRole;
import com.example.smoking.platform.repository.UserRepository;

@Service // Đánh dấu đây là một Spring Service Component
public class UserService {

    @Autowired // Tự động tiêm (inject) UserRepository vào đây
    private UserRepository userRepository;

    @Autowired 
    private PasswordEncoder passwordEncoder;

    // Phương thức tạo người dùng mới
    public User createUser(User user) {
        // Bạn có thể thêm logic kiểm tra trùng lặp username/email ở đây
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }
        // Trong thực tế, mật khẩu cần được mã hóa trước khi lưu
        user.setPassword(passwordEncoder.encode(user.getPassword())); 
        user.setRegistrationDate(LocalDateTime.now());
        user.setActive(true);
        return userRepository.save(user);
    }

    // Phương thức tìm người dùng theo ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Phương thức tìm người dùng theo username
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Phương thức lấy tất cả người dùng
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Phương thức cập nhật người dùng
    public User updateUser(User user) {
        // Kiểm tra xem người dùng có tồn tại không trước khi cập nhật
        if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User with ID " + user.getId() + " not found.");
        }
        user.setLastLoginDate(LocalDateTime.now()); // Ví dụ: cập nhật last login khi update
        return userRepository.save(user);
    }

    // Phương thức mới để cập nhật thông tin cai thuốc
    public User updateCessationInfo(Long userId, LocalDate quitStartDate, Integer cigarettesPerDay, Double costPerPack, String cessationGoal, LocalDate expectedQuitDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));

        user.setQuitStartDate(quitStartDate);
        user.setCigarettesPerDay(cigarettesPerDay);
        user.setCostPerPack(costPerPack);
        user.setCessationGoal(cessationGoal); // Thêm cập nhật mục tiêu cai thuốc
        user.setExpectedQuitDate(expectedQuitDate); // Thêm cập nhật ngày dự kiến cai

        // Bạn có thể thêm logic kiểm tra hoặc cập nhật `currentSmokingStatus` tại đây
        // Ví dụ: nếu quitStartDate được set, có thể chuyển currentSmokingStatus sang "Quit Attempt" hoặc tương tự
        // Hoặc để trống và người dùng sẽ cập nhật nó riêng.
        // Tạm thời, chúng ta sẽ không tự động cập nhật `currentSmokingStatus` ở đây để giữ đơn giản.

        return userRepository.save(user);
    }

    // Phương thức để cập nhật mật khẩu, nếu cần
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Phương thức xóa người dùng
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // Phương thức ví dụ để thêm User mẫu khi khởi động ứng dụng
    public void addSampleUsers() {
        // Chỉ thêm nếu chưa có người dùng nào (để tránh lỗi khi chạy lại)
        if (userRepository.count() == 0) {
            System.out.println("Adding sample users...");
            // Member
            User member = new User("member_user", "member123", "member@example.com", UserRole.MEMBER);
            member.setFullName("Nguyen Van A");
            member.setGender("Male");
            member.setDateOfBirth(LocalDate.of(1995, 5, 10));
            member.setCurrentSmokingStatus("Daily");
            member.setCigarettesPerDay(20);
            member.setCostPerPack(30000.0);
            member.setCessationGoal("Vi suc khoe gia dinh");
            member.setQuitStartDate(LocalDate.of(2025, 6, 1));
            member.setExpectedQuitDate(LocalDate.of(2025, 12, 1));
            createUser(member); // createUser sẽ tự động mã hóa mật khẩu

            // Coach
            User coach = new User("coach_john", "coach123", "john.coach@example.com", UserRole.COACH);
            coach.setFullName("John Smith");
            createUser(coach); // createUser sẽ tự động mã hóa mật khẩu

            // Admin
            User admin = new User("admin_user", "admin123", "admin@example.com", UserRole.ADMIN);
            admin.setFullName("Admin System");
            createUser(admin); // createUser sẽ tự động mã hóa mật khẩu

            // Guest (chỉ ví dụ, thực tế Guest không lưu vào DB thường)
            User guest = new User("guest_visitor", "guest123", "guest@example.com", UserRole.GUEST);
            guest.setFullName("Guest User");
            createUser(guest); // createUser sẽ tự động mã hóa mật khẩu

            System.out.println("Sample users added successfully.");
        }
    }
}