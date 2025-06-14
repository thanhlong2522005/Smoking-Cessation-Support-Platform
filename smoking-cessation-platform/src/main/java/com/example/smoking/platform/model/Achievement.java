package com.example.smoking.platform.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "achievements")
@Data
@NoArgsConstructor
@AllArgsConstructor 
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Tên huy hiệu, ví dụ: "1-Day Free"

    @Column(nullable = false)
    private String description; // Mô tả huy hiệu, ví dụ: "Đã không hút thuốc trong 1 ngày"

    @Column(nullable = true)
    private String iconUrl; // URL đến biểu tượng huy hiệu (nếu có)

    @Enumerated(EnumType.STRING) // Thêm trường type để phân loại huy hiệu
    @Column(nullable = false, columnDefinition = "MANUAL") // Mặc định là MANUAL
    private AchievementType type;

    // Constructors

    public enum AchievementType {
        MANUAL, // Admin cấp thủ công
        TIME_BASED, // Huy hiệu dựa trên thời gian không hút thuốc (1 ngày, 1 tuần,...)
        MONEY_BASED, // Huy hiệu dựa trên số tiền tiết kiệm được
        GOAL_BASED // Huy hiệu dựa trên việc hoàn thành mục tiêu cai thuốc
    }
    
    public Achievement(String name, String description, String iconUrl) {
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}