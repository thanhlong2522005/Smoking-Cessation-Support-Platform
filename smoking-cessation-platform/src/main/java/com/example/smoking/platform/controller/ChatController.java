package com.example.smoking.platform.controller;

import com.example.smoking.platform.model.Message;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.service.MessageService;
import com.example.smoking.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String chatPage(Model model, Authentication authentication) {
        User currentUser = userService.getUserByUsername(authentication.getName()).orElseThrow();
        List<Message> messages = messageService.getMessages(currentUser);
        model.addAttribute("messages", messages);
        model.addAttribute("newMessage", new Message());
        return "chat";
    }

    @PostMapping("/send")
    public String sendMessage(@ModelAttribute Message newMessage, Authentication authentication) {
        User sender = userService.getUserByUsername(authentication.getName()).orElseThrow();
        User coach = userService.getAllUsers().stream()
                .filter(u -> u.getRole().toString().equals("COACH"))
                .findFirst()
                .orElseThrow(); // Tạm lấy coach đầu tiên

        messageService.sendMessage(sender, coach, newMessage.getContent());
        return "redirect:/chat";
    }
}

