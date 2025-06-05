package com.example.smoking.platform.service;


import com.example.smoking.platform.model.SmokingLog;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.repository.SmokingLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SmokingLogService {
    @Autowired
    private SmokingLogRepository smokingLogRepository;

    public SmokingLog save(SmokingLog smokingLog) {
        smokingLog.setDate(LocalDateTime.now());
        return smokingLogRepository.save(smokingLog);
    }

    public List<SmokingLog> getLogsByUser(User user) {
        return smokingLogRepository.findByUser(user);
    }
}
