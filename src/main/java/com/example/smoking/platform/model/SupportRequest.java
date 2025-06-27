package com.example.smoking.platform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SupportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String subject;

    private String type;

    @Column(length = 2000)
    private String message;

    private LocalDateTime createdAt;

    // ✅ Constructor mặc định (bắt buộc để JPA hoạt động)
    public SupportRequest() {
        this.createdAt = LocalDateTime.now();
    }

    // ✅ Getter và Setter đầy đủ

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
