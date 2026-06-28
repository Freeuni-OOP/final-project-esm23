package com.quizwebsite.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class FriendshipTest {

	private Friendship friendship;
	private LocalDateTime now;

	@BeforeEach
	public void setUp() {
		now = LocalDateTime.now();
		friendship = new Friendship(1, 2, FriendshipStatus.ACCEPTED, now);
	}

	@Test
	public void testFullConstructor() {
		assertEquals(1, friendship.getUserId());
		assertEquals(2, friendship.getFriendId());
		assertEquals(FriendshipStatus.ACCEPTED, friendship.getStatus());
		assertEquals(now, friendship.getCreatedAt());
	}

	@Test
	public void testInsertConstructor() {
		Friendship f = new Friendship(1, 2);
		assertEquals(FriendshipStatus.PENDING, f.getStatus());
		assertNull(f.getCreatedAt());
	}

	@Test
	public void testSetStatus() {
		Friendship f = new Friendship(1, 2);
		f.setStatus(FriendshipStatus.ACCEPTED);
		assertEquals(FriendshipStatus.ACCEPTED, f.getStatus());
	}
}