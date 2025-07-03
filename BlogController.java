package com.example.smoking.platform.controller;

import com.example.smoking.platform.model.BlogPost;
import com.example.smoking.platform.model.Comment;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.service.BlogService;
import com.example.smoking.platform.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    // ✅ Hiển thị danh sách bài viết đã được duyệt
    @GetMapping("/blogs")
    public String listBlogs(Model model) {
        List<BlogPost> posts = blogService.getApprovedPosts();
        model.addAttribute("blogs", posts);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Optional<User> currentUser = userService.getUserByUsername(currentUsername);
        currentUser.ifPresent(user -> model.addAttribute("userRole", user.getRole().toString()));

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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Optional<User> currentUser = userService.getUserByUsername(currentUsername);
        currentUser.ifPresent(user -> model.addAttribute("userRole", user.getRole().toString()));

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

    // ✅ Xóa bài viết (chỉ tác giả hoặc admin)
    @GetMapping("/blog/delete/{id}")
    public String deleteBlog(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        BlogPost blog = blogService.getById(id);

        if (blog == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bài viết không tồn tại.");
            return "redirect:/blogs";
        }

        String username = principal != null ? principal.getName() : "";
        Optional<User> currentUser = userService.getUserByUsername(username);

        // Kiểm tra quyền
        if (currentUser.isPresent()) {
            User user = currentUser.get();
            boolean isAuthor = blog.getAuthor().equals(username);
            boolean isAdmin = user.getRole().toString().equals("ADMIN");

            if (isAuthor || isAdmin) {
                blogService.deleteById(id);
                redirectAttributes.addFlashAttribute("successMessage", "Xóa bài viết thành công.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền xóa bài viết này.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không xác định được người dùng.");
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
