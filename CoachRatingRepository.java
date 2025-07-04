package com.example.smoking.platform.repository;

import com.example.smoking.platform.model.CoachRating;
import com.example.smoking.platform.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoachRatingRepository extends JpaRepository<CoachRating, Long> {
    List<CoachRating> findBySession(ChatSession session);
}
