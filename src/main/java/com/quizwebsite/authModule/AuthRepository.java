package com.quizwebsite.authModule;

import java.util.Optional;

public interface AuthRepository {

    boolean existsByUsername(String username);

    Optional<AuthUser> findByUsername(String username);

    void save(AuthUser user);
}