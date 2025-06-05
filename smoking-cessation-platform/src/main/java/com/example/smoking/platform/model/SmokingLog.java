package com.example.smoking.platform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SmokingLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime date;
    private int cigarettesSmoked;
    private double costPerCigarette;
    private String frequency;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public int getCigarettesSmoked() { return cigarettesSmoked; }
    public void setCigarettesSmoked(int cigarettesSmoked) { this.cigarettesSmoked = cigarettesSmoked; }
    public double getCostPerCigarette() { return costPerCigarette; }
    public void setCostPerCigarette(double costPerCigarette) { this.costPerCigarette = costPerCigarette; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
}