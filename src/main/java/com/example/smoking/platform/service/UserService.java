package com.example.smoking.platform.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.smoking.platform.model.User;
import com.example.smoking.platform.model.UserRole;
import com.example.smoking.platform.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired 
    private PasswordEncoder passwordEncoder;

    // Đăng ký người dùng mới
    public void registerUser(User user) {
        // Check trùng username hoặc email
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại!");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng!");
        }

        // Set mặc định cho tài khoản mới
        user.setRole(UserRole.MEMBER); // hoặc lựa chọn role nếu cho chọn
        user.setRegistrationDate(LocalDateTime.now());
        user.setActive(true); // nếu chưa cần xác thực email

        // Mã hóa mật khẩu
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Lưu
        userRepository.save(user);
    }


    // ✅ Tạo user dùng cho demo hoặc admin
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRegistrationDate(LocalDateTime.now());
        user.setActive(true);
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("Không tìm thấy người dùng với ID: " + user.getId());
        }
        user.setLastLoginDate(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User updateCessationInfo(Long userId, LocalDate quitStartDate, Integer cigarettesPerDay, Double costPerPack, String cessationGoal, LocalDate expectedQuitDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));

        user.setQuitStartDate(quitStartDate);
        user.setCigarettesPerDay(cigarettesPerDay);
        user.setCostPerPack(costPerPack);
        user.setCessationGoal(cessationGoal);
        user.setExpectedQuitDate(expectedQuitDate);

        return userRepository.save(user);
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // ✅ Dữ liệu mẫu nếu chưa có user
    public void addSampleUsers() {
        if (userRepository.count() == 0) {
            System.out.println("Đang thêm tài khoản mẫu...");

            User member = new User("member_user", "member123", "member@example.com", UserRole.MEMBER);
            member.setFullName("Nguyen Van A");
            member.setGender("Male");
            member.setDateOfBirth(LocalDate.of(1995, 5, 10));
            member.setCurrentSmokingStatus("Daily");
            member.setCigarettesPerDay(20);
            member.setCostPerPack(30000.0);
            member.setCessationGoal("Vì sức khỏe gia đình");
            member.setQuitStartDate(LocalDate.of(2025, 6, 1));
            member.setExpectedQuitDate(LocalDate.of(2025, 12, 1));
            createUser(member);

            User coach = new User("coach_john", "coach123", "john.coach@example.com", UserRole.COACH);
            coach.setFullName("John Smith");
            createUser(coach);

            User admin = new User("admin_user", "admin123", "admin@example.com", UserRole.ADMIN);
            admin.setFullName("Admin System");
            createUser(admin);

            User guest = new User("guest_visitor", "guest123", "guest@example.com", UserRole.GUEST);
            guest.setFullName("Guest User");
            createUser(guest);

            System.out.println("Tạo tài khoản mẫu thành công.");
        }
    }
}
