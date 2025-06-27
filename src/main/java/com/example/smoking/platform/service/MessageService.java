package com.example.smoking.platform.service;

import com.example.smoking.platform.model.Message;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public void sendMessage(User sender, User receiver, String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());
        messageRepository.save(message);
    }

    public List<Message> getMessages(User user) {
        return messageRepository.findBySenderOrReceiver(user, user);
    }
}
