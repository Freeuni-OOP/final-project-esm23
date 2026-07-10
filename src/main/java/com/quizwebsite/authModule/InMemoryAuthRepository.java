package com.quizwebsite.authModule;

import com.quizwebsite.model.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

// Fast in-memory Authrepository used for unit-testing Authhservice without hitting MySQL
public class InMemoryAuthRepository implements AuthRepository {
    private final Map<String, User> usersByUsername = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public boolean existsByUsername(String username) {
        return usersByUsername.containsKey(normalizeUsername(username));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(normalizeUsername(username)));
    }

    @Override
    public User save(User user) {
        User stored = new User(
                idGenerator.getAndIncrement(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getSalt(),
                user.isAdmin(),
                LocalDateTime.now()
        );
        usersByUsername.put(normalizeUsername(user.getUsername()), stored);
        return stored;
    }

    private String normalizeUsername(String username) {
        return username.trim().toLowerCase();
    }
}