package com.example.smoking.platform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    @JsonIgnore // Ngăn Jackson tuần tự hóa trường user để tránh tham chiếu vòng
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