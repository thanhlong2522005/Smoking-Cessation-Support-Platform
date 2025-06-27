package com.example.smoking.platform.controller;

import com.example.smoking.platform.model.BlogPost;
import com.example.smoking.platform.model.Comment;
import com.example.smoking.platform.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class BlogController {

    @Autowired
    private BlogService blogService;

    // ✅ Hiển thị danh sách bài viết đã được duyệt
    @GetMapping("/blogs")
    public String listBlogs(Model model) {
        List<BlogPost> posts = blogService.getApprovedPosts();
        model.addAttribute("blogs", posts);
        return "blog-list";
    }

    // ✅ Trang chi tiết bài viết (có comment)
    @GetMapping("/blogs/{id}")
    public String viewBlog(@PathVariable Long id, Model model) {
        BlogPost post = blogService.getById(id);
        if (post == null) {
            model.addAttribute("errorMessage", "Không tìm thấy bài viết.");
            return "redirect:/blogs";
        }

        List<Comment> comments = blogService.getCommentsForPost(post);
        model.addAttribute("blog", post);
        model.addAttribute("comments", comments);
        model.addAttribute("newComment", new Comment()); // Gửi form comment
        return "blog-detail";
    }

    // ✅ Trang viết blog mới
    @GetMapping("/blogs/write")
    public String writeBlogForm(Model model) {
        model.addAttribute("blogPost", new BlogPost());
        return "blog-write";
    }

    // ✅ Xử lý gửi blog mới
    @PostMapping("/blogs/write")
    public String submitBlog(@ModelAttribute BlogPost blogPost, Principal principal) {
        blogPost.setAuthor(principal != null ? principal.getName() : "Ẩn danh");
        blogService.savePost(blogPost);
        return "redirect:/blogs";
    }

    // Xóa bài viết (admin hoặc tác giả)
    @GetMapping("/blog/delete/{id}")
    public String deleteBlog(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            blogService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa bài viết thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa bài viết.");
        }  
        return "redirect:/blogs";
    }


    // ✅ Gửi bình luận
    @PostMapping("/blogs/{id}/comment")
    public String submitComment(@PathVariable Long id,
                                 @ModelAttribute("newComment") Comment comment,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        BlogPost post = blogService.getById(id);
        if (post == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy bài viết.");
            return "redirect:/blogs";
        }

        comment.setBlogPost(post);
        comment.setAuthor(principal != null ? principal.getName() : "Ẩn danh");
        comment.setCreatedAt(LocalDateTime.now());

        blogService.saveComment(comment);
        redirectAttributes.addFlashAttribute("commentSuccess", "Đã gửi bình luận thành công!");
        return "redirect:/blogs/" + id;
    }
}
