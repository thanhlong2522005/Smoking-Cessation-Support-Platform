package com.example.smoking; // Điều chỉnh package name cho đúng với dự án của bạn

import com.example.smoking.dao.UserDAO;
import com.example.smoking.model.User;
import com.example.smoking.model.UserRole; // Import UserRole enum
import java.time.LocalDateTime;
import java.time.LocalDate; // Import LocalDate
import java.util.List;
import java.util.Optional; // Import Optional

public class App { // Hoặc class Main, tùy thuộc vào tên file chính của bạn

    public static void main(String[] args) {
        System.out.println("Bắt đầu ứng dụng Smoking Cessation Support Platform...");

        // Khởi tạo DAO với tên persistence unit đã định nghĩa trong persistence.xml
        // "JPAs" là tên persistence-unit trong persistence.xml của bạn
        UserDAO userDAO = new UserDAO("JPAs");

        try {
            // --- 1. Tạo và Lưu một User mới (Role: MEMBER) ---
            System.out.println("\n--- Đang tạo và lưu User (Member) mới ---");
            User memberUser = new User();
            memberUser.setUsername("member_user");
            memberUser.setPassword("hashed_member_password"); // Trong thực tế, bạn sẽ hash mật khẩu
            memberUser.setEmail("member@example.com");
            memberUser.setFullName("Nguyen Van A");
            memberUser.setGender("Male");
            memberUser.setDateOfBirth(LocalDate.of(1995, 5, 10));
            memberUser.setRole(UserRole.MEMBER);
            memberUser.setRegistrationDate(LocalDateTime.now());
            memberUser.setActive(true);
            memberUser.setCurrentSmokingStatus("Daily");
            memberUser.setCigarettesPerDay(20);
            memberUser.setCostPerPack(30000.0);
            memberUser.setCessationGoal("Vì sức khỏe gia đình");
            memberUser.setQuitStartDate(LocalDate.of(2025, 6, 1));
            memberUser.setExpectedQuitDate(LocalDate.of(2025, 12, 1));

            Optional<User> savedMemberUser = userDAO.save(memberUser);
            savedMemberUser.ifPresentOrElse(
                user -> System.out.println("Lưu User (Member) thành công: " + user),
                () -> System.out.println("Lưu User (Member) thất bại.")
            );

            // --- 2. Tạo và Lưu một User mới (Role: COACH) ---
            System.out.println("\n--- Đang tạo và lưu User (Coach) mới ---");
            User coachUser = new User();
            coachUser.setUsername("coach_john");
            coachUser.setPassword("hashed_coach_password");
            coachUser.setEmail("john.coach@example.com");
            coachUser.setFullName("John Smith");
            coachUser.setRole(UserRole.COACH);
            coachUser.setRegistrationDate(LocalDateTime.now());
            coachUser.setActive(true);

            Optional<User> savedCoachUser = userDAO.save(coachUser);
            savedCoachUser.ifPresentOrElse(
                user -> System.out.println("Lưu User (Coach) thành công: " + user),
                () -> System.out.println("Lưu User (Coach) thất bại.")
            );

            // --- 3. Tìm User theo ID ---
            if (savedMemberUser.isPresent()) {
                Long memberUserId = savedMemberUser.get().getId();
                System.out.println("\n--- Đang tìm User theo ID: " + memberUserId + " ---");
                Optional<User> foundUserById = userDAO.findById(memberUserId);
                foundUserById.ifPresentOrElse(
                    user -> System.out.println("Tìm thấy User theo ID: " + user),
                    () -> System.out.println("Không tìm thấy User với ID: " + memberUserId)
                );
            }

            // --- 4. Tìm User theo Username ---
            String usernameToFind = "coach_john";
            System.out.println("\n--- Đang tìm User theo Username: " + usernameToFind + " ---");
            Optional<User> foundUserByUsername = userDAO.findByUsername(usernameToFind);
            foundUserByUsername.ifPresentOrElse(
                user -> System.out.println("Tìm thấy User theo Username: " + user),
                () -> System.out.println("Không tìm thấy User với Username: " + usernameToFind)
            );

            // --- 5. Cập nhật User ---
            if (savedMemberUser.isPresent()) {
                User userToUpdate = savedMemberUser.get();
                userToUpdate.setEmail("member_updated@example.com");
                userToUpdate.setLastLoginDate(LocalDateTime.now());
                System.out.println("\n--- Đang cập nhật User: " + userToUpdate.getUsername() + " ---");
                Optional<User> updatedUser = userDAO.update(userToUpdate);
                updatedUser.ifPresentOrElse(
                    user -> System.out.println("Cập nhật User thành công: " + user),
                    () -> System.out.println("Cập nhật User thất bại.")
                );
            }

            // --- 6. Lấy tất cả User ---
            System.out.println("\n--- Tất cả User trong DB: ---");
            List<User> allUsers = userDAO.findAll();
            if (!allUsers.isEmpty()) {
                allUsers.forEach(System.out::println);
            } else {
                System.out.println("Không có User nào trong DB.");
            }

            // --- 7. Xóa User (tùy chọn, để thử nghiệm) ---
            if (savedMemberUser.isPresent()) {
                Long memberUserIdToDelete = savedMemberUser.get().getId();
                System.out.println("\n--- Đang xóa User với ID: " + memberUserIdToDelete + " ---");
                if (userDAO.delete(memberUserIdToDelete)) {
                    System.out.println("Xóa User thành công.");
                } else {
                    System.out.println("Xóa User thất bại hoặc không tìm thấy.");
                }
            }

            // Kiểm tra lại sau khi xóa
            System.out.println("\n--- Tất cả User sau khi xóa: ---");
            List<User> usersAfterDelete = userDAO.findAll();
            if (!usersAfterDelete.isEmpty()) {
                usersAfterDelete.forEach(System.out::println);
            } else {
                System.out.println("Không có User nào trong DB.");
            }


        } catch (Exception e) {
            System.err.println("Lỗi xảy ra trong quá trình chạy ứng dụng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Quan trọng: Đóng EntityManagerFactory khi kết thúc ứng dụng
            UserDAO.closeEntityManagerFactory();
            System.out.println("\nỨng dụng kết thúc.");
        }
    }
}