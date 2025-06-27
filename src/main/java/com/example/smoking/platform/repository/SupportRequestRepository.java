package com.example.smoking.platform.repository;

import com.example.smoking.platform.model.SupportRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {
}

