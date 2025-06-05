package com.example.smoking.platform.controller;

import com.example.smoking.platform.model.SmokingLog;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.service.SmokingLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/smoking")
public class SmokingController {
    @Autowired
    private SmokingLogService smokingLogService;

    @PostMapping("/log")
    public ResponseEntity<?> logSmoking(@RequestBody SmokingLog log) {
        User user = log.getUser();
        if (user == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "User is required");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        log.setUser(user);
        SmokingLog savedLog = smokingLogService.save(log);
        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("message", "Smoking log recorded with ID: " + savedLog.getId());
        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/logs")
    public ResponseEntity<?> getSmokingLogs(@RequestParam Long userId) {
        User user = new User();
        user.setId(userId);
        List<SmokingLog> logs = smokingLogService.getLogsByUser(user);
        return ResponseEntity.ok(logs);
    }
}