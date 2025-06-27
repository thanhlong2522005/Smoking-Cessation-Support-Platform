package com.example.smoking.platform.service;

import com.example.smoking.platform.model.Motivation;
import com.example.smoking.platform.repository.MotivationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class MotivationService {

    @Autowired
    private MotivationRepository repository;

    public void save(Motivation motivation) {
        repository.save(motivation);
    }

    public Motivation getRandomMotivation() {
        List<Motivation> all = repository.findAll();
        if (all.isEmpty()) return new Motivation("Hãy cố gắng! Mỗi ngày không hút thuốc là một chiến thắng.", "Hệ thống");
        return all.get(new Random().nextInt(all.size()));
    }
}
