package com.example.smoking.platform.service;

import com.example.smoking.platform.model.*;
import com.example.smoking.platform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ChatSessionRepository chatSessionRepo;

    @Autowired
    private ChatMessageRepository chatMessageRepo;

    @Autowired
    private CoachRatingRepository coachRatingRepo;

    // Tạo phiên chat mới
    public ChatSession createSession(User member, User coach) {
        ChatSession session = new ChatSession();
        session.setMember(member);
        session.setCoach(coach);
        session.setStartTime(LocalDateTime.now());
        return chatSessionRepo.save(session);
    }

    // Gửi tin nhắn
    public ChatMessage sendMessage(ChatSession session, User sender, String content) {
        ChatMessage message = new ChatMessage();
        message.setSession(session);
        message.setSender(sender);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        return chatMessageRepo.save(message);
    }

    public List<ChatMessage> getMessages(ChatSession session) {
        return chatMessageRepo.findBySessionOrderByTimestampAsc(session);
    }

    public Optional<ChatSession> getSessionById(Long id) {
        return chatSessionRepo.findById(id);
    }

    public List<ChatSession> getSessionsByMember(User member) {
        return chatSessionRepo.findByMember(member);
    }

    public List<ChatSession> getSessionsByCoach(User coach) {
        return chatSessionRepo.findByCoach(coach);
    }

    public void endSession(ChatSession session) {
        session.setEnded(true);
        session.setEndTime(LocalDateTime.now());
        chatSessionRepo.save(session);
    }

    public CoachRating rateCoach(ChatSession session, User rater, int stars, String comment) {
        CoachRating rating = new CoachRating();
        rating.setSession(session);
        rating.setRating(stars);
        rating.setComment(comment);
        rating.setRatedBy(rater);
        rating.setRatedAt(LocalDateTime.now());
        return coachRatingRepo.save(rating);
    }

    public List<CoachRating> getRatingsBySession(ChatSession session) {
        return coachRatingRepo.findBySession(session);
    }
}
