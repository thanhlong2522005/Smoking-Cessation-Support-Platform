package com.example.smoking.platform.repository;

import com.example.smoking.platform.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findByApprovedTrue(); // Chỉ lấy bài đã duyệt
}
