package com.quizwebsite.authModule;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionManager {
    private final Map<String, Session> sessions = new HashMap<>();

    public Session createSession(AuthUser user) {
        Session session = new Session(user);
        sessions.put(session.getToken(), session);
        return session;
    }

    public Optional<Session> getSession(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return Optional.ofNullable(sessions.get(token));
    }

    public boolean isLoggedIn(String token) {
        return getSession(token).isPresent();
    }

    public void logout(String token) {
        if (token != null) {
            sessions.remove(token);
        }
    }
}