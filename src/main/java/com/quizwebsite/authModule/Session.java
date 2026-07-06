package com.quizwebsite.authModule;

import com.quizwebsite.model.User;
import java.time.LocalDateTime;
import java.util.UUID;

public class Session {
    private final String token;
    private final User user;
    private final LocalDateTime createdAt;

    public Session(User user) {
        this.token = UUID.randomUUID().toString();
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}