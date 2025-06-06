package com.example.smoking.platform.repository;

import com.example.smoking.platform.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    // Có thể thêm các phương thức tìm kiếm tùy chỉnh ở đây nếu cần,
    // ví dụ: List<Feedback> findByIsRead(boolean isRead);
}