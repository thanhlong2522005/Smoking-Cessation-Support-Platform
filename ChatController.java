package com.example.smoking.platform.controller;

import com.example.smoking.platform.model.*;
import com.example.smoking.platform.service.ChatService;
import com.example.smoking.platform.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    // ✅ Trang chọn coach để bắt đầu chat
    @GetMapping("/select")
    public String selectCoach(Model model) {
        List<User> coaches = userService.getAllByRole("COACH");
        model.addAttribute("coaches", coaches);
        return "chat-select";
    }

    // ✅ Bắt đầu phiên chat với coach
    @GetMapping("/start/{coachId}")
    public String startChat(@PathVariable Long coachId,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {
        Optional<User> memberOpt = userService.getUserByUsername(principal.getName());
        Optional<User> coachOpt = userService.getUserById(coachId);

        if (memberOpt.isEmpty() || coachOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng hoặc coach.");
            return "redirect:/chat/select";
        }

        ChatSession session = chatService.createSession(memberOpt.get(), coachOpt.get());
        return "redirect:/chat/session/" + session.getId();
    }

    // ✅ Trang chat chính
    @GetMapping("/session/{id}")
    public String viewChat(@PathVariable Long id,
                           Model model,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        Optional<ChatSession> sessionOpt = chatService.getSessionById(id);
        Optional<User> userOpt = userService.getUserByUsername(principal.getName());

        if (sessionOpt.isEmpty() || userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy phiên trò chuyện hoặc người dùng.");
            return "redirect:/chat/select";
        }

        ChatSession session = sessionOpt.get();

        model.addAttribute("chatSession", session);
        model.addAttribute("messages", chatService.getMessages(session));
        model.addAttribute("newMessage", new ChatMessage());
        model.addAttribute("username", userOpt.get().getUsername());

        return "chat-session";
    }

    // ✅ Gửi tin nhắn
    @PostMapping("/session/{id}/send")
    public String sendMessage(@PathVariable Long id,
                              @RequestParam("content") String content,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        Optional<ChatSession> sessionOpt = chatService.getSessionById(id);
        Optional<User> senderOpt = userService.getUserByUsername(principal.getName());

        if (sessionOpt.isEmpty() || senderOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể gửi tin nhắn. Phiên hoặc người dùng không tồn tại.");
            return "redirect:/chat/select";
        }

        chatService.sendMessage(sessionOpt.get(), senderOpt.get(), content);
        return "redirect:/chat/session/" + id;
    }

    // ✅ Kết thúc phiên trò chuyện
    @PostMapping("/session/{id}/end")
    public String endChat(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        Optional<ChatSession> sessionOpt = chatService.getSessionById(id);

        if (sessionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy phiên chat.");
            return "redirect:/chat/select";
        }

        chatService.endSession(sessionOpt.get());
        return "redirect:/chat/session/" + id + "/rate";
    }

    // ✅ Trang đánh giá coach
    @GetMapping("/session/{id}/rate")
    public String showRatingForm(@PathVariable Long id,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        Optional<ChatSession> sessionOpt = chatService.getSessionById(id);

        if (sessionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy phiên để đánh giá.");
            return "redirect:/chat/select";
        }

        model.addAttribute("sessionId", id);
        return "chat-rate";
    }

    // ✅ Gửi đánh giá
    @PostMapping("/session/{id}/rate")
    public String submitRating(@PathVariable Long id,
                               @RequestParam("stars") int stars,
                               @RequestParam("comment") String comment,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        Optional<ChatSession> sessionOpt = chatService.getSessionById(id);
        Optional<User> raterOpt = userService.getUserByUsername(principal.getName());

        if (sessionOpt.isEmpty() || raterOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể đánh giá. Thiếu thông tin.");
            return "redirect:/chat/select";
        }

        chatService.rateCoach(sessionOpt.get(), raterOpt.get(), stars, comment);
        return "redirect:/chat/select?success";
    }
}
