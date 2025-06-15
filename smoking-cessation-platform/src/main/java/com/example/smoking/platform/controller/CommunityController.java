package com.example.smoking.controller;

import com.example.smoking.model.*;
import com.example.smoking.service.*;
import org.springframework.web.bind.annotation.*;
import com.example.smoking.platform.model.Blog;
import java.util.List;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private final BlogService blogService;
    private final BadgeService badgeService;
    private final ChatService chatService;

    public CommunityController(BlogService blogService, BadgeService badgeService, ChatService chatService) {
        this.blogService = blogService;
        this.badgeService = badgeService;
        this.chatService = chatService;
    }

    @GetMapping("/blogs")
    public List<Blog> getBlogs() {
        return blogService.getAllBlogs();
    }

    @PostMapping("/blogs")
    public Blog postBlog(@RequestBody Blog blog) {
        return blogService.createBlog(blog);
    }

    @GetMapping("/badges")
    public List<Badge> getBadges() {
        return badgeService.getAllBadges();
    }

    @PostMapping("/chat")
    public ChatMessage sendMessage(@RequestBody ChatMessage msg) {
        return chatService.sendMessage(msg);
    }

    @GetMapping("/chat")
    public List<ChatMessage> getMessages(@RequestParam String user1, @RequestParam String user2) {
        return chatService.getMessages(user1, user2);
    }
}
