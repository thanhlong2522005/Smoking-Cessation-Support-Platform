package com.example.smoking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sender;
    private String receiver;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();

    // Getters and setters
}
