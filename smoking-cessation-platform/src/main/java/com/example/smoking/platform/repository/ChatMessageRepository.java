package com.example.smoking.repository;

import com.example.smoking.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderAndReceiverOrReceiverAndSender(
        String sender1, String receiver1, String sender2, String receiver2);
}
