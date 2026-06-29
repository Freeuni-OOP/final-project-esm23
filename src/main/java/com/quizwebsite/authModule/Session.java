package com.quizwebsite.authModule;

import java.time.LocalDateTime;
import java.util.UUID;

public class Session {
    private final String token;
    private final AuthUser user;
    private final LocalDateTime createdAt;

    public Session(AuthUser user) {
        this.token = UUID.randomUUID().toString();
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    public String getToken() {
        return token;
    }

    public AuthUser getUser() {
        return user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}