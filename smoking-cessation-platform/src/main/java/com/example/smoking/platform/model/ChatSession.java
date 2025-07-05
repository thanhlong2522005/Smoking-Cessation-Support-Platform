package com.example.smoking.platform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private User member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id")
    private User coach;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean ended = false;

    public ChatSession() {}

    // Getters & Setters
    public Long getId() { return id; }
    public User getMember() { return member; }
    public void setMember(User member) { this.member = member; }
    public User getCoach() { return coach; }
    public void setCoach(User coach) { this.coach = coach; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public boolean isEnded() { return ended; }
    public void setEnded(boolean ended) { this.ended = ended; }
}
