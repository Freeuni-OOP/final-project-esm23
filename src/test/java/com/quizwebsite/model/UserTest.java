package com.quizwebsite.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

	private User user;
	private LocalDateTime now;

	@BeforeEach
	public void setUp() {
		now = LocalDateTime.now();
		user = new User(1L, "luka", "hash123", "salt123", false, now);
	}

	@Test
	public void testFullConstructor() {
		assertEquals(1L, user.getId());
		assertEquals("luka", user.getUsername());
		assertEquals("hash123", user.getPasswordHash());
		assertEquals("salt123", user.getSalt());
		assertFalse(user.isAdmin());
		assertEquals(now, user.getCreatedAt());
	}

	@Test
	public void testInsertConstructor() {
		User newUser = new User("luka", "hash123", "salt123");
		assertEquals(0L, newUser.getId());
		assertNull(newUser.getCreatedAt());
		assertFalse(newUser.isAdmin());
	}

	@Test
	public void testSetAdmin() {
		user.setAdmin(true);
		assertTrue(user.isAdmin());

		user.setAdmin(false);
		assertFalse(user.isAdmin());
	}
}