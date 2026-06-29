package com.quizwebsite.authModule;

import java.util.Optional;
// handles all authentication-related operations.
public class AuthService {
    // repository for user data
    private final AuthRepository authRepository;
    // handles hashing and salting of passwords
    private final PasswordHasher passwordHasher;
    // manages user sessions after successful authentication
    private final SessionManager sessionManager;
    // deff constructor
    public AuthService() {
        this.authRepository = new InMemoryAuthRepository();
        this.passwordHasher = new PasswordHasher();
        this.sessionManager = new SessionManager();
    }

    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
        this.passwordHasher = new PasswordHasher();
        this.sessionManager = new SessionManager();
    }
    // registers new user this method also validates input if username already exists, hashes the password and save its data
    public AuthResult register(String username, String password) {
        String validationError = validateRegistrationInput(username, password);
        // CHECKING IF ITS VALID OR NOT
        if (validationError != null) {
            return AuthResult.failure(validationError);
        }
        // normilizing username so Luka and luka are treated the same
        String normalizedUsername = normalizeUsername(username);
        // if it already exists
        if (authRepository.existsByUsername(normalizedUsername)) {
            return AuthResult.failure("Username already exists.");
        }

        String salt = passwordHasher.generateSalt();
        String passwordHash = passwordHasher.hashPassword(password, salt);

        AuthUser user = new AuthUser(normalizedUsername, passwordHash, salt);
        authRepository.save(user);

        return AuthResult.success("Registration successful.", user, null);
    }
    // log in an existing user the method checks the user and pass, and verifies it and after that creates session and at lat returns session token
    public AuthResult login(String username, String password) {
        if (username == null || username.isBlank()) {
            return AuthResult.failure("Username is required.");
        }

        if (password == null || password.isBlank()) {
            return AuthResult.failure("Password is required.");
        }

        String normalizedUsername = normalizeUsername(username);
        Optional<AuthUser> optionalUser = authRepository.findByUsername(normalizedUsername);

        if (optionalUser.isEmpty()) {
            return AuthResult.failure("Invalid username or password.");
        }

        AuthUser user = optionalUser.get();

        boolean passwordMatches = passwordHasher.verifyPassword(
                password,
                user.getSalt(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            return AuthResult.failure("Invalid username or password.");
        }

        Session session = sessionManager.createSession(user);

        return AuthResult.success("Login successful.", user, session.getToken());
    }

    public void logout(String sessionToken) {
        sessionManager.logout(sessionToken);
    }

    public boolean isLoggedIn(String sessionToken) {
        return sessionManager.isLoggedIn(sessionToken);
    }

    public Optional<AuthUser> getCurrentUser(String sessionToken) {
        return sessionManager.getSession(sessionToken).map(Session::getUser);
    }
    // validates registration input, return error if it is invalid
    private String validateRegistrationInput(String username, String password) {
        if (username == null || username.isBlank()) {
            return "Username is required.";
        }

        if (username.trim().length() < 3) {
            return "Username must be at least 3 characters.";
        }

        if (password == null || password.isBlank()) {
            return "Password is required.";
        }

        if (password.length() < 6) {
            return "Password must be at least 6 characters.";
        }

        return null;
    }

    private String normalizeUsername(String username) {
        return username.trim().toLowerCase();
    }
}