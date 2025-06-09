package com.example.smoking.platform.model;

import jakarta.persistence.*;

@Entity
@Table(name = "achievements")
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

    // Constructors
    public Achievement() {
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