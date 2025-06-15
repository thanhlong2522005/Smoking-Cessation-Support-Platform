
package com.example.smoking.platform.model;

import com.example.smoking.platform.model.User;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    private User author;

    // Getters and setters
}