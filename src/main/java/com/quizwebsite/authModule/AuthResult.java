package com.quizwebsite.authModule;

public class AuthResult {

    // Indicates whether the auth attempt was successful or not
    private final boolean success;
    // A message to be displayed to the user, if it was success or if it was error
    private final String message;
    // The user object if the auth attempt was successful, otherwise null
    private final AuthUser user;
    // The session token if the auth attempt was successful, otherwise null
    private final String sessionToken;
    // construct
    private AuthResult(boolean success, String message, AuthUser user, String sessionToken) {
        this.success = success;
        this.message = message;
        this.user = user;
        this.sessionToken = sessionToken;
    }
    // creates a success result
    public static AuthResult success(String message, AuthUser user, String sessionToken) {
        return new AuthResult(true, message, user, sessionToken);
    }
    // failed authentication reuslt , in this case user and session tokens are null
    public static AuthResult failure(String message) {
        return new AuthResult(false, message, null, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public AuthUser getUser() {
        return user;
    }

    public String getSessionToken() {
        return sessionToken;
    }
}