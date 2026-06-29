package com.quizwebsite.authModule;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryAuthRepository implements AuthRepository {
    private final Map<String, AuthUser> usersByUsername = new HashMap<>();

    @Override
    public boolean existsByUsername(String username) {
        return usersByUsername.containsKey(normalizeUsername(username));
    }

    @Override
    public Optional<AuthUser> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(normalizeUsername(username)));
    }

    @Override
    public void save(AuthUser user) {
        usersByUsername.put(normalizeUsername(user.getUsername()), user);
    }

    private String normalizeUsername(String username) {
        return username.trim().toLowerCase();
    }
}