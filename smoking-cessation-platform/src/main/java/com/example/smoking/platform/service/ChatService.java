package com.example.smoking.service;

import com.example.smoking.model.ChatMessage;
import com.example.smoking.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    private final ChatMessageRepository chatRepository;

    public ChatService(ChatMessageRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public List<ChatMessage> getMessages(String user1, String user2) {
        return chatRepository.findBySenderAndReceiverOrReceiverAndSender(user1, user2, user1, user2);
    }

    public ChatMessage sendMessage(ChatMessage msg) {
        return chatRepository.save(msg);
    }
}
