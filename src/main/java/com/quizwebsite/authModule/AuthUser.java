package com.quizwebsite.authModule;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class AuthUser {
    // unique identifier for the user
    private final UUID id;
    // username is unique, but case-insensitive.
    private final String username;
    // hashed version of the password
    private final String passwordHash;
    // salt is used to generate a unique hash for each user so it is more secure
    private final String salt;
    // date and time when the user was created
    private final LocalDateTime createdAt;

    public AuthUser(String username, String passwordHash, String salt) {
        // generate a random UUID
        this.id = UUID.randomUUID();
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    String getPasswordHash() {
        return passwordHash;
    }

    String getSalt() {
        return salt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object object) {
        // if both objects are the same instance, they are equal
        if (this == object) {
            return true;
        }
        // if the object is not authUser, they can not be equal
        if (!(object instanceof AuthUser authUser)) {
            return false;
        }

        return Objects.equals(id, authUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
