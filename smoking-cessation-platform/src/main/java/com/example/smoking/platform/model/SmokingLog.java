package com.example.smoking.platform.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "smoking_log")
@Data
@NoArgsConstructor
public class SmokingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    @Positive(message = "Số điếu phải lớn hơn 0")
    private int cigarettesSmoked;

    @Column(nullable = false)
    @Positive(message = "Giá tiền phải lớn hơn 0")
    private double costPerCigarette;

    @Column(length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    // Enum cho tần suất
    public enum Frequency {
        DAILY, OCCASIONAL, WEEKLY
    }

    public SmokingLog(User user, LocalDateTime date, int cigarettesSmoked, double costPerCigarette, Frequency frequency) {
        this.user = user;
        this.date = date;
        this.cigarettesSmoked = cigarettesSmoked;
        this.costPerCigarette = costPerCigarette;
        this.frequency = frequency;
    }
}