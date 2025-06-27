package com.example.smoking.platform.service;

import com.example.smoking.platform.model.BlogPost;
import com.example.smoking.platform.model.Comment;
import com.example.smoking.platform.repository.BlogPostRepository;
import com.example.smoking.platform.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogService {

    @Autowired
    private BlogPostRepository blogRepository;

    @Autowired
    private CommentRepository commentRepo;

    // Lưu bài viết
    public void savePost(BlogPost post) {
        blogRepository.save(post);
    }

    // Lấy danh sách bài đã duyệt
    public List<BlogPost> getApprovedPosts() {
        return blogRepository.findByApprovedTrue();
    }

    public void deleteById(Long id) {
        blogRepository.deleteById(id);
    }


    // Lấy bài viết theo ID
    public BlogPost getById(Long id) {
        return blogRepository.findById(id).orElse(null);
    }

    // Lấy comment của 1 bài viết
    public List<Comment> getCommentsForPost(BlogPost post) {
        return commentRepo.findByBlogPostOrderByCreatedAtDesc(post);
    }

    // Lưu comment
    public void saveComment(Comment comment) {
        commentRepo.save(comment);
    }
}
