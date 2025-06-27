package com.example.smoking.platform.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quit_plans")
@Data
@NoArgsConstructor
public class QuitPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Lý do cai thuốc là bắt buộc")
    @Column(nullable = false)
    private String reason;

    @NotNull(message = "Ngày bắt đầu là bắt buộc")
    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @ElementCollection
    @CollectionTable(name = "quit_plan_phases", joinColumns = @JoinColumn(name = "quit_plan_id"))
    @Column(name = "phase_description")
    private List<String> phases;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PLANNING, IN_PROGRESS, COMPLETED, FAILED
    }

    public QuitPlan(User user, String reason, LocalDateTime startDate, LocalDateTime endDate, List<String> phases, Status status) {
        this.user = user;
        this.reason = reason;
        this.startDate = startDate;
        this.endDate = endDate;
        this.phases = phases;
        this.status = status;
    }
}