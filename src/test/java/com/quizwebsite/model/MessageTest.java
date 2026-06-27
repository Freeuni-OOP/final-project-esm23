package com.quizwebsite.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

	private Message message;
	private LocalDateTime now;

	@BeforeEach
	public void setUp() {
		now = LocalDateTime.now();
		message = new Message(1, 2, 3, MessageType.NOTE, "Good game!", null, false, now);
	}

	@Test
	public void testFullConstructor() {
		assertEquals(1, message.getId());
		assertEquals(2, message.getSenderId());
		assertEquals(3, message.getRecipientId());
		assertEquals(MessageType.NOTE, message.getType());
		assertEquals("Good game!", message.getBody());
		assertNull(message.getQuizId());
		assertFalse(message.isRead());
		assertEquals(now, message.getSentAt());
	}

	@Test
	public void testInsertConstructor() {
		Message m = new Message(2, 3, MessageType.FRIEND_REQUEST, null, null);
		assertEquals(0, m.getId());
		assertFalse(m.isRead());
		assertNull(m.getSentAt());
	}

	@Test
	public void testChallengeHasQuizId() {
		Message m = new Message(2, 3, MessageType.CHALLENGE, "Beat my score!", 500L);
		assertEquals(500L, m.getQuizId());
	}

	@Test
	public void testSetRead() {
		message.setRead(true);
		assertTrue(message.isRead());
	}
}