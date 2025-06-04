package com.example.smoking.model; 

import javax.persistence.*;
import java.time.LocalDateTime; 
import java.util.Set; 

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder; 

@Entity
@Table(name = "users") // Tên bảng trong database
@Data // Tạo getter, setter, toString, equals, hashCode (từ Lombok)
@NoArgsConstructor // Tạo constructor không tham số (từ Lombok)
@AllArgsConstructor // Tạo constructor với tất cả các tham số (từ Lombok)
@Builder // Cung cấp builder pattern để tạo đối tượng dễ dàng hơn
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng ID
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 255) // Nên lưu mật khẩu đã hash
    private String password;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Column
    private String gender; // Ví dụ: "Male", "Female", "Other"

    @Column(name = "date_of_birth")
    private java.time.LocalDate dateOfBirth; // Sử dụng LocalDate cho ngày sinh

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // Lưu dưới dạng chuỗi (MEMBER, COACH, ADMIN, GUEST)
    private UserRole role; // Enum cho các vai trò (Member, Coach, Admin)

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate; // Thời gian đăng ký

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate; // Thời gian đăng nhập cuối cùng

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true; // Trạng thái tài khoản

    // --- Các trường thông tin liên quan đến cai thuốc (chỉ dành cho Member) ---
    @Column(name = "current_smoking_status")
    private String currentSmokingStatus; // Ví dụ: "Daily", "Occasional", "Quit"

    @Column(name = "cigarettes_per_day")
    private Integer cigarettesPerDay; // Số điếu thuốc hút mỗi ngày trước khi cai

    @Column(name = "cost_per_pack")
    private Double costPerPack; // Chi phí một gói thuốc

    @Column(name = "cessation_goal")
    private String cessationGoal; // Lý do cai thuốc, mục tiêu

    @Column(name = "quit_start_date")
    private java.time.LocalDate quitStartDate; // Ngày bắt đầu cai

    @Column(name = "expected_quit_date")
    private java.time.LocalDate expectedQuitDate; // Ngày dự kiến cai được


    // Quan hệ OneToOne với MemberPackage (nếu Member chỉ có 1 gói thành viên)
    // @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private MemberPackage memberPackage; // Sẽ được định nghĩa trong class MemberPackage

    // Quan hệ OneToMany với CessationPlan (một User có thể có nhiều kế hoạch cai thuốc)
    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private Set<CessationPlan> cessationPlans; // Sẽ được định nghĩa trong class CessationPlan

    // Quan hệ OneToMany với ProgressLog (một User có nhiều log tiến trình)
    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private Set<ProgressLog> progressLogs; // Sẽ được định nghĩa trong class ProgressLog

    // Quan hệ OneToMany với AchievementBadge (User có thể nhận nhiều huy hiệu)
    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private Set<AchievementBadge> achievementBadges; // Sẽ được định nghĩa trong class AchievementBadge

    // Quan hệ ManyToMany với Coaches (Nếu Member có thể có nhiều Coach hoặc ngược lại)
    // @ManyToMany
    // @JoinTable(
    //     name = "user_coach",
    //     joinColumns = @JoinColumn(name = "user_id"),
    //     inverseJoinColumns = @JoinColumn(name = "coach_id")
    // )
    // private Set<User> coaches; // Nếu Coach cũng là một User với role COACH

    // Hoặc, nếu Coach là một Entity riêng:
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "coach_id") // Khoá ngoại đến bảng coaches
    // private Coach assignedCoach; // Sẽ được định nghĩa trong class Coach

    // Mối quan hệ với Rating và Feedback (nếu User có thể gửi/nhận rating/feedback)
    // @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private Set<Feedback> sentFeedbacks;

    // @OneToMany(mappedBy = "reviewedUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private Set<Feedback> receivedFeedbacks;
}