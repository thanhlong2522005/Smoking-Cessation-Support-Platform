package com.example.smoking.platform.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Set;
import java.util.HashSet;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users") // Tên bảng trong database
@Data // Lombok: Tự động tạo getter, setter, toString, equals, hashCode
@NoArgsConstructor // Lombok: Tự động tạo constructor không tham số
@AllArgsConstructor // Lombok: Tự động tạo constructor với tất cả các tham số
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng ID
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    private String gender; // Male, Female, Other

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING) // Lưu Enum dưới dạng String trong DB
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    private LocalDateTime lastLoginDate;

    @Column(nullable = false)
    private boolean isActive; // Trạng thái tài khoản

    // Các thuộc tính liên quan đến việc cai thuốc (chỉ áp dụng cho MEMBER)
    private String currentSmokingStatus; // Ví dụ: Daily, Occasional, Quit
    private Integer cigarettesPerDay; // Số điếu/ngày trước khi cai
    private Double costPerPack; // Giá tiền 1 gói thuốc
    
    // THÊM TRƯỜNG cigarettesPerPack VÀO ĐÂY
    private Integer cigarettesPerPack; // Số điếu thuốc trong một gói

    private String cessationGoal; // Lý do/mục tiêu cai thuốc
    private LocalDate quitStartDate; // Thời điểm bắt đầu cai
    private LocalDate expectedQuitDate; // Thời điểm dự kiến cai được

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private Set<Feedback> feedbacks = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserAchievement> userAchievements = new HashSet<>();

<<<<<<< HEAD
    @ManyToOne
    @JoinColumn(name = "membership_package_id")
    private MembershipPackage membershipPackage;

    // Getter/Setter
    public MembershipPackage getMembershipPackage() {
        return membershipPackage;
    }

    public void setMembershipPackage(MembershipPackage membershipPackage) {
        this.membershipPackage = membershipPackage;
    }

=======
>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2
    // Constructor tùy chỉnh nếu cần (Lombok @AllArgsConstructor đã bao gồm tất cả các trường)
    // Nếu bạn muốn một constructor chỉ với một số trường, bạn có thể tạo thủ công hoặc dùng @Builder của Lombok
    // Constructor bạn đã cung cấp chỉ bao gồm một phần của các trường
    // Nếu bạn muốn giữ constructor này, hãy đảm bảo các trường khác được xử lý (ví dụ: gán giá trị mặc định)
    public User(String username, String password, String email, UserRole role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.registrationDate = LocalDateTime.now();
        this.isActive = true;
        // Các trường khác như cigarettesPerDay, costPerPack, cigarettesPerPack, etc. sẽ là null hoặc giá trị mặc định của kiểu dữ liệu.
        // Bạn có thể cần gán giá trị mặc định cho chúng ở đây nếu chúng không được phép null.
    }
}