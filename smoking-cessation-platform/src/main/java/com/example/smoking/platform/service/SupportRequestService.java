package com.example.smoking.platform.service;

import com.example.smoking.platform.model.SupportRequest;
import com.example.smoking.platform.repository.SupportRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupportRequestService {

    @Autowired
    private SupportRequestRepository repository;

    public void saveNewRequest(String subject, String type, String message, String username) {
        SupportRequest request = new SupportRequest();
        request.setUsername(username);
        request.setSubject(subject);
        request.setType(type);
        request.setMessage(message);
        repository.save(request);
    }
}
