package com.quizwebsite.authModule;

import com.quizwebsite.model.User;

public class AuthResult {

    // Indicates whether the auth attempt was successful or not
    private final boolean success;
    // A message to be displayed to the user, if it was success or if it was error
    private final String message;
    // The user object if the auth attempt was successful, otherwise null
    private final User user;

    private AuthResult(boolean success, String message, User user) {
        this.success = success;
        this.message = message;
        this.user = user;
    }

    // creates a success result
    public static AuthResult success(String message, User user) {
        return new AuthResult(true, message, user);
    }

    // failed authentication reuslt , in this case user and session tokens are null
    public static AuthResult failure(String message) {
        return new AuthResult(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}