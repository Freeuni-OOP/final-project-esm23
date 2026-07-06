package com.quizwebsite.authModule;

import com.quizwebsite.model.User;
import com.quizwebsite.util.DBConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// tests for JdbcAuthRepository against the test db

public class JdbcAuthRepositoryTest {

	private AuthRepository repository;

	@BeforeEach
	public void setUp() throws SQLException {
		repository = new JdbcAuthRepository();
		try (Connection conn = DBConnection.get();
				 Statement st = conn.createStatement()) {
			st.executeUpdate("DELETE FROM users");
		}
	}

	@Test
	public void testUnknownUserDoesNotExist() {
		assertFalse(repository.existsByUsername("luka"));
	}

	@Test
	public void testSavedUserExists() {
		repository.save(new User("luka", "hash", "salt"));

		assertTrue(repository.existsByUsername("luka"));
	}

	@Test
	public void testFindingUnknownUserReturnsEmpty() {
		Optional<User> found = repository.findByUsername("ghost");

		assertTrue(found.isEmpty());
	}

	@Test
	public void testSavedUserCanBeFound() {
		repository.save(new User("luka", "hash", "salt"));

		Optional<User> found = repository.findByUsername("luka");

		assertTrue(found.isPresent());
		assertEquals("luka", found.get().getUsername());
		assertEquals("hash", found.get().getPasswordHash());
		assertEquals("salt", found.get().getSalt());
	}

	@Test
	public void testSaveAssignsIdAndCreatedAt() {
		User saved = repository.save(new User("luka", "hash", "salt"));

		assertTrue(saved.getId() > 0);
		assertNotNull(saved.getCreatedAt());
		assertFalse(saved.isAdmin());
	}

	@Test
	public void testDuplicateUsernameIsRejected() {
		repository.save(new User("luka", "hash", "salt"));

		assertThrows(RuntimeException.class,
						() -> repository.save(new User("luka", "hash2", "salt2")));
	}
}