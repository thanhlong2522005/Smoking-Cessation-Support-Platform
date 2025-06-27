package com.example.smoking.platform.repository;

import com.example.smoking.platform.model.Comment;
import com.example.smoking.platform.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBlogPostOrderByCreatedAtDesc(BlogPost blogPost);
}
