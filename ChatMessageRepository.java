package com.example.smoking.platform.repository;

import com.example.smoking.platform.model.ChatMessage;
import com.example.smoking.platform.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySessionOrderByTimestampAsc(ChatSession session);
}
