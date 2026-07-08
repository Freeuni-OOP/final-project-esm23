package com.quizwebsite.authModule;

import com.quizwebsite.model.User;

import java.util.Optional;

public interface AuthRepository {

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    User save(User user);
}