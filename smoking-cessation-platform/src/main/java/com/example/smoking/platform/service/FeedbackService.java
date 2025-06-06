package com.example.smoking.platform.service;

import com.example.smoking.platform.model.Feedback;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    // Lưu một phản hồi mới
    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    // Lấy tất cả các phản hồi (cho Admin)
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    // Lấy một phản hồi theo ID
    public Optional<Feedback> getFeedbackById(Long id) {
        return feedbackRepository.findById(id);
    }

    // Đánh dấu phản hồi là đã đọc
    public Feedback markAsRead(Long feedbackId) {
        Optional<Feedback> optionalFeedback = feedbackRepository.findById(feedbackId);
        if (optionalFeedback.isPresent()) {
            Feedback feedback = optionalFeedback.get();
            feedback.setRead(true);
            return feedbackRepository.save(feedback);
        }
        return null; // Hoặc throw exception nếu không tìm thấy
    }

    // Xóa phản hồi (cho Admin)
    public void deleteFeedback(Long feedbackId) {
        feedbackRepository.deleteById(feedbackId);
    }
}