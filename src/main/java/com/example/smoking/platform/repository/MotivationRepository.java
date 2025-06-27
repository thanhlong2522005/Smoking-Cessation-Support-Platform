package com.example.smoking.platform.repository;

import com.example.smoking.platform.model.Motivation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MotivationRepository extends JpaRepository<Motivation, Long> {
}
