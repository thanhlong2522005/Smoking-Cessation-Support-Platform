package com.example.smoking.platform.repository;

import com.example.smoking.platform.model.Rating;
import com.example.smoking.platform.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findAllByOrderBySubmittedAtDesc();
    Optional<Rating> findByUser(User user);
}
