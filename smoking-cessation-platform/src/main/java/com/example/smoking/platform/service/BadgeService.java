package com.example.smoking.service;

import com.example.smoking.model.Badge;
import com.example.smoking.repository.BadgeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BadgeService {
    private final BadgeRepository badgeRepository;

    public BadgeService(BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
    }

    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }
}
