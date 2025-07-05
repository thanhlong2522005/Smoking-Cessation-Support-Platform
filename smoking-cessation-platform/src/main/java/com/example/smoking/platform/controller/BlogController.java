package com.example.smoking.platform.controller;

import com.example.smoking.platform.model.*;
import com.example.smoking.platform.service.*;

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

    @Autowired
    private MotivationService motivationService;

    @Autowired
    private AchievementService achievementService;

    // ✅ Hiển thị danh sách blog + danh sách huy hiệu để chia sẻ
    @GetMapping("/blogs")
    public String listBlogs(Model model) {
        List<BlogPost> posts = blogService.getApprovedPosts();
        model.addAttribute("blogs", posts);

        // Truyền danh sách huy hiệu vào blog-list.html
        List<Achievement> achievements = achievementService.getAllAchievements();
        model.addAttribute("achievements", achievements);

        // Lấy role người dùng để xử lý phân quyền giao diện
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Optional<User> currentUser = userService.getUserByUsername(currentUsername);
        currentUser.ifPresent(user -> model.addAttribute("userRole", user.getRole().toString()));

        return "blog-list";
    }

    // ✅ Trang chi tiết bài viết
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
        model.addAttribute("newComment", new Comment());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Optional<User> currentUser = userService.getUserByUsername(currentUsername);
        currentUser.ifPresent(user -> model.addAttribute("userRole", user.getRole().toString()));

        return "blog-detail";
    }

    // ✅ Viết bài chia sẻ
    @GetMapping("/blogs/write")
    public String writeBlogForm(Model model) {
        model.addAttribute("blogPost", new BlogPost());
        return "blog-write";
    }

    @PostMapping("/blogs/write")
    public String submitBlog(@ModelAttribute BlogPost blogPost, Principal principal) {
        blogPost.setAuthor(principal != null ? principal.getName() : "Ẩn danh");
        blogService.savePost(blogPost);
        return "redirect:/blogs";
    }

    // ✅ Xóa bài viết
    @GetMapping("/blog/delete/{id}")
    public String deleteBlog(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        BlogPost blog = blogService.getById(id);

        if (blog == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bài viết không tồn tại.");
            return "redirect:/blogs";
        }

        String username = principal != null ? principal.getName() : "";
        Optional<User> currentUser = userService.getUserByUsername(username);

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

    // ✅ Gửi lời động viên (dùng author là tên người gửi)
    @PostMapping("/blog/sendMotivation")
    public String sendMotivation(@RequestParam String receiverUsername,
                             @RequestParam String message,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        String senderUsername = principal.getName();

        // Ghép tên người gửi và người nhận nếu muốn
        String fullMessage = "Gửi đến " + receiverUsername + ": " + message;

        Motivation motivation = new Motivation();
        motivation.setAuthor(senderUsername);
        motivation.setMessage(fullMessage);

        motivationService.save(motivation);
        redirectAttributes.addFlashAttribute("successMessage", "💌 Đã gửi lời động viên!");

        return "redirect:/blogs";
    }


    // ✅ Chia sẻ huy hiệu
    @PostMapping("/blog/shareAchievement")
    public String shareAchievement(@RequestParam String receiverUsername,
                                   @RequestParam Long achievementId,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        String senderUsername = principal.getName();
        Optional<User> senderOpt = userService.getUserByUsername(senderUsername);
        Optional<User> receiverOpt = userService.getUserByUsername(receiverUsername);
        Optional<Achievement> achievementOpt = achievementService.getAchievementById(achievementId);

        if (senderOpt.isPresent() && receiverOpt.isPresent() && achievementOpt.isPresent()) {
            String result = achievementService.grantAchievementToUser(receiverOpt.get(), achievementOpt.get());
            if (result == null) {
                redirectAttributes.addFlashAttribute("successMessage", "🏅 Đã chia sẻ huy hiệu cho " + receiverUsername + "!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", result);
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể chia sẻ huy hiệu. Thông tin không hợp lệ.");
        }

        return "redirect:/blogs";
    }
}
