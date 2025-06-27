package com.example.smoking.platform.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
<<<<<<< HEAD
import java.util.List;
import java.util.Optional;

import com.example.smoking.platform.model.User;
import com.example.smoking.platform.model.UserRole;
import com.example.smoking.platform.repository.UserRepository;

=======
import java.util.List; // Import UserRole
import java.util.Optional;

>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

<<<<<<< HEAD
@Service
public class UserService {

    @Autowired
=======
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.model.UserRole;
import com.example.smoking.platform.repository.UserRepository;

@Service // Đánh dấu đây là một Spring Service Component
public class UserService {

    @Autowired // Tự động tiêm (inject) UserRepository vào đây
>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2
    private UserRepository userRepository;

    @Autowired 
    private PasswordEncoder passwordEncoder;

<<<<<<< HEAD
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
=======
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
>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2
        user.setRegistrationDate(LocalDateTime.now());
        user.setActive(true);
        return userRepository.save(user);
    }

<<<<<<< HEAD
=======
    // Phương thức tìm người dùng theo ID
>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

<<<<<<< HEAD
=======
    // Phương thức tìm người dùng theo username
>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

<<<<<<< HEAD
=======
    // Phương thức lấy tất cả người dùng
>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

<<<<<<< HEAD
    public User updateUser(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("Không tìm thấy người dùng với ID: " + user.getId());
        }
        user.setLastLoginDate(LocalDateTime.now());
        return userRepository.save(user);
    }

=======
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
>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2
    public User updateCessationInfo(Long userId, LocalDate quitStartDate, Integer cigarettesPerDay, Double costPerPack, String cessationGoal, LocalDate expectedQuitDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));

        user.setQuitStartDate(quitStartDate);
        user.setCigarettesPerDay(cigarettesPerDay);
        user.setCostPerPack(costPerPack);
<<<<<<< HEAD
        user.setCessationGoal(cessationGoal);
        user.setExpectedQuitDate(expectedQuitDate);
=======
        user.setCessationGoal(cessationGoal); // Thêm cập nhật mục tiêu cai thuốc
        user.setExpectedQuitDate(expectedQuitDate); // Thêm cập nhật ngày dự kiến cai

        // Bạn có thể thêm logic kiểm tra hoặc cập nhật `currentSmokingStatus` tại đây
        // Ví dụ: nếu quitStartDate được set, có thể chuyển currentSmokingStatus sang "Quit Attempt" hoặc tương tự
        // Hoặc để trống và người dùng sẽ cập nhật nó riêng.
        // Tạm thời, chúng ta sẽ không tự động cập nhật `currentSmokingStatus` ở đây để giữ đơn giản.
>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2

        return userRepository.save(user);
    }

<<<<<<< HEAD
=======
    // Phương thức để cập nhật mật khẩu, nếu cần
>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

<<<<<<< HEAD
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

=======
    // Phương thức xóa người dùng
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

<<<<<<< HEAD
    // ✅ Dữ liệu mẫu nếu chưa có user
    public void addSampleUsers() {
        if (userRepository.count() == 0) {
            System.out.println("Đang thêm tài khoản mẫu...");

=======
    // Phương thức ví dụ để thêm User mẫu khi khởi động ứng dụng
    public void addSampleUsers() {
        // Chỉ thêm nếu chưa có người dùng nào (để tránh lỗi khi chạy lại)
        if (userRepository.count() == 0) {
            System.out.println("Adding sample users...");
            // Member
>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2
            User member = new User("member_user", "member123", "member@example.com", UserRole.MEMBER);
            member.setFullName("Nguyen Van A");
            member.setGender("Male");
            member.setDateOfBirth(LocalDate.of(1995, 5, 10));
            member.setCurrentSmokingStatus("Daily");
            member.setCigarettesPerDay(20);
            member.setCostPerPack(30000.0);
<<<<<<< HEAD
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
=======
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
>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2
