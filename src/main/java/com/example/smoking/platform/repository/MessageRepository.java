package com.example.smoking.platform.repository;

import com.example.smoking.platform.model.Message;
import com.example.smoking.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderOrReceiver(User sender, User receiver);
}
