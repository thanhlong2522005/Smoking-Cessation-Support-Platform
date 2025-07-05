package com.example.smoking.platform.repository;

import com.example.smoking.platform.model.ChatSession;
import com.example.smoking.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    List<ChatSession> findByCoach(User coach);
    List<ChatSession> findByMember(User member);
}
