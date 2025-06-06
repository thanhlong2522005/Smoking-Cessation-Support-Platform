package com.example.smoking.platform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY) // Many feedbacks to one user
    @JoinColumn(name = "user_id", nullable = false) // Foreign key to User table
    private User user;

    @Column(nullable = false)
    private LocalDateTime submissionDate;

    @Column(nullable = false)
    private boolean isRead; // Trạng thái đã đọc/chưa đọc

    // Constructors
    public Feedback() {
        this.submissionDate = LocalDateTime.now(); // Tự động set ngày gửi
        this.isRead = false; // Mặc định chưa đọc
    }

    public Feedback(String content, User user) {
        this.content = content;
        this.user = user;
        this.submissionDate = LocalDateTime.now();
        this.isRead = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}