package com.quizwebsite.authModule;

import com.quizwebsite.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the authentication module.
 *
 * This test class verifies:
 * - user registration
 * - login validation
 * - logout and session handling
 * - password hashing behavior
 * - session model behavior
 */

 // Uses InMemoryAuthRepository so no real database is touched
 // JdbcAuthRepositoryTest for the DB-backed integration tests.

public class AuthServiceTest {

    private AuthService authService;

    @BeforeEach
    public void setUp() {
        authService = new AuthService(new InMemoryAuthRepository());
    }

    // Registration tests

    @Test
    public void registerShouldCreateUserWhenInputIsValid() {
        AuthResult result = authService.register("luka", "password123");

        assertTrue(result.isSuccess());
        assertEquals("Registration successful.", result.getMessage());
        assertNotNull(result.getUser());
        assertEquals("luka", result.getUser().getUsername());
    }

    @Test
    public void registerShouldFailWhenUsernameAlreadyExists() {
        authService.register("luka", "password123");
        AuthResult secondResult = authService.register("luka", "password123");

        assertFalse(secondResult.isSuccess());
        assertEquals("Username already exists.", secondResult.getMessage());
    }

    @Test
    public void registerShouldFailWhenUsernameIsTooShort() {
        AuthResult result = authService.register("lu", "password123");

        assertFalse(result.isSuccess());
        assertEquals("Username must be at least 3 characters.", result.getMessage());
    }

    @Test
    public void registerShouldFailWhenPasswordIsTooShort() {
        AuthResult result = authService.register("luka", "123");

        assertFalse(result.isSuccess());
        assertEquals("Password must be at least 6 characters.", result.getMessage());
    }


    // Login tests


    @Test
    public void loginShouldReturnSessionTokenWhenCredentialsAreCorrect() {
        authService.register("luka", "password123");
        AuthResult loginResult = authService.login("luka", "password123");

        assertTrue(loginResult.isSuccess());
        assertEquals("Login successful.", loginResult.getMessage());
        assertNotNull(loginResult.getSessionToken());
        assertTrue(authService.isLoggedIn(loginResult.getSessionToken()));
    }

    @Test
    public void loginShouldFailWhenPasswordIsWrong() {
        authService.register("luka", "password123");
        AuthResult loginResult = authService.login("luka", "wrongpassword");

        assertFalse(loginResult.isSuccess());
        assertEquals("Invalid username or password.", loginResult.getMessage());
        assertNull(loginResult.getSessionToken());
    }

    @Test
    public void loginShouldFailWhenUserDoesNotExist() {
        AuthResult loginResult = authService.login("unknown", "password123");

        assertFalse(loginResult.isSuccess());
        assertEquals("Invalid username or password.", loginResult.getMessage());
        assertNull(loginResult.getSessionToken());
    }


    // Session and logout tests


    @Test
    public void logoutShouldRemoveActiveSession() {
        authService.register("luka", "password123");
        AuthResult loginResult = authService.login("luka", "password123");

        String token = loginResult.getSessionToken();

        assertTrue(authService.isLoggedIn(token));

        authService.logout(token);

        assertFalse(authService.isLoggedIn(token));
    }

    @Test
    public void getCurrentUserShouldReturnLoggedInUser() {
        authService.register("luka", "password123");
        AuthResult loginResult = authService.login("luka", "password123");

        String token = loginResult.getSessionToken();

        assertTrue(authService.getCurrentUser(token).isPresent());
        assertEquals("luka", authService.getCurrentUser(token).get().getUsername());
    }


    // SessionManager tests


    @Test
    public void sessionShouldReturnAllSessionFields() {
        User user = new User("luka", "hash", "salt");
        Session session = new Session(user);

        assertNotNull(session.getToken());
        assertEquals(user, session.getUser());
        assertNotNull(session.getCreatedAt());
    }

    @Test
    public void sessionManagerShouldCreateAndFindSession() {
        SessionManager sessionManager = new SessionManager();
        User user = new User("luka", "hash", "salt");

        Session session = sessionManager.createSession(user);

        assertNotNull(session.getToken());
        assertTrue(sessionManager.getSession(session.getToken()).isPresent());
        assertEquals(user, sessionManager.getSession(session.getToken()).get().getUser());
    }

    @Test
    public void sessionManagerShouldReturnEmptyForNullToken() {
        SessionManager sessionManager = new SessionManager();

        assertTrue(sessionManager.getSession(null).isEmpty());
    }

    @Test
    public void sessionManagerShouldReturnEmptyForBlankToken() {
        SessionManager sessionManager = new SessionManager();

        assertTrue(sessionManager.getSession("   ").isEmpty());
    }

    @Test
    public void sessionManagerShouldReturnEmptyForUnknownToken() {
        SessionManager sessionManager = new SessionManager();

        assertTrue(sessionManager.getSession("unknown-token").isEmpty());
    }

    @Test
    public void sessionManagerLogoutShouldRemoveSession() {
        SessionManager sessionManager = new SessionManager();
        User user = new User("luka", "hash", "salt");

        Session session = sessionManager.createSession(user);

        assertTrue(sessionManager.isLoggedIn(session.getToken()));

        sessionManager.logout(session.getToken());

        assertFalse(sessionManager.isLoggedIn(session.getToken()));
    }

    @Test
    public void sessionManagerLogoutShouldNotFailForNullToken() {
        SessionManager sessionManager = new SessionManager();

        assertDoesNotThrow(() -> sessionManager.logout(null));
    }


    // AuthService integration tests (still in-memory; see JdbcAuthRepositoryTest for real-DB coverage)

    @Test
    public void authServiceShouldRejectDuplicateUsernameIgnoringCaseAndSpaces() {
        AuthResult firstResult = authService.register("  Luka  ", "password123");
        AuthResult secondResult = authService.register("luka", "password123");

        assertTrue(firstResult.isSuccess());
        assertFalse(secondResult.isSuccess());
        assertEquals("Username already exists.", secondResult.getMessage());
    }

    @Test
    public void getCurrentUserShouldReturnEmptyWhenTokenIsNull() {
        assertTrue(authService.getCurrentUser(null).isEmpty());
    }

    @Test
    public void getCurrentUserShouldReturnEmptyWhenTokenIsBlank() {
        assertTrue(authService.getCurrentUser("   ").isEmpty());
    }

    @Test
    public void getCurrentUserShouldReturnEmptyWhenTokenIsInvalid() {
        assertTrue(authService.getCurrentUser("invalid-token").isEmpty());
    }

    // Password hashing tests


    @Test
    public void passwordHasherShouldGenerateDifferentSalts() {
        PasswordHasher passwordHasher = new PasswordHasher();

        String firstSalt = passwordHasher.generateSalt();
        String secondSalt = passwordHasher.generateSalt();

        assertNotNull(firstSalt);
        assertNotNull(secondSalt);
        assertNotEquals(firstSalt, secondSalt);
    }

    @Test
    public void passwordHasherShouldReturnDifferentHashForDifferentSalt() {
        PasswordHasher passwordHasher = new PasswordHasher();

        String firstSalt = passwordHasher.generateSalt();
        String secondSalt = passwordHasher.generateSalt();

        String firstHash = passwordHasher.hashPassword("password123", firstSalt);
        String secondHash = passwordHasher.hashPassword("password123", secondSalt);

        assertNotEquals(firstHash, secondHash);
    }
}
