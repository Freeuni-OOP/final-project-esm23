package com.quizwebsite.authModule;

import com.quizwebsite.model.User;

import java.util.Optional;
// handles all authentication-related operations.
public class AuthService {
    // repository for user data
    private final AuthRepository authRepository;
    // handles hashing and salting of passwords
    private final PasswordHasher passwordHasher;

    // deff constructor
    public AuthService() {
        this(new JdbcAuthRepository());
    }

    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
        this.passwordHasher = new PasswordHasher();
    }

    // registers new user this method also validates input if username already exists, hashes the password and save its data
    public AuthResult register(String username, String password) {
        String validationError = validateRegistrationInput(username, password);
        if (validationError != null) {
            return AuthResult.failure(validationError);
        }
        // normilizing username so Luka and luka are treated the same
        String normalizedUsername = normalizeUsername(username);

        if (authRepository.existsByUsername(normalizedUsername)) {
            return AuthResult.failure("Username already exists.");
        }

        String salt = passwordHasher.generateSalt();
        String passwordHash = passwordHasher.hashPassword(password, salt);

        User user = new User(normalizedUsername, passwordHash, salt);
        User savedUser = authRepository.save(user);

        return AuthResult.success("Registration successful.", savedUser);
    }

    // log in an existing user, checks the username and password and verifies the hash matches
    public AuthResult login(String username, String password) {
        if (username == null || username.isBlank()) {
            return AuthResult.failure("Username is required.");
        }

        if (password == null || password.isBlank()) {
            return AuthResult.failure("Password is required.");
        }
        String normalizedUsername = normalizeUsername(username);
        Optional<User> optionalUser = authRepository.findByUsername(normalizedUsername);
        if (optionalUser.isEmpty()) {
            return AuthResult.failure("Invalid username or password.");
        }
        User user = optionalUser.get();
        boolean passwordMatches = passwordHasher.verifyPassword(
                password,
                user.getSalt(),
                user.getPasswordHash()
        );
        if (!passwordMatches) {
            return AuthResult.failure("Invalid username or password.");
        }
        return AuthResult.success("Login successful.", user);
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