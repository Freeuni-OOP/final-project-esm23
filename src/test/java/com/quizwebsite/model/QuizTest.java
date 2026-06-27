package com.quizwebsite.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class QuizTest {

	private Quiz quiz;
	private LocalDateTime now;

	@BeforeEach
	public void setUp() {
		now = LocalDateTime.now();
		quiz = new Quiz(1, 1, "History Quiz", "A quiz about history", true,
						false, true, true, now);
	}

	@Test
	public void testFullConstructor() {
		assertEquals(1, quiz.getId());
		assertEquals(1, quiz.getCreatorId());
		assertEquals("History Quiz", quiz.getTitle());
		assertEquals("A quiz about history", quiz.getDescription());
		assertTrue(quiz.isRandomOrder());
		assertFalse(quiz.isOnePage());
		assertTrue(quiz.isImmediateCorrection());
		assertTrue(quiz.isPracticeEnabled());
		assertEquals(now, quiz.getCreatedAt());
	}

	@Test
	public void testInsertConstructor() {
		Quiz newQuiz = new Quiz(2, "History Quiz", "A quiz about history", false,
						true, false, false);
		assertEquals(0, newQuiz.getId());
		assertNull(newQuiz.getCreatedAt());
	}

	@Test
	public void testSetters() {
		quiz.setTitle("New Title");
		assertEquals("New Title", quiz.getTitle());

		quiz.setDescription("New desc");
		assertEquals("New desc", quiz.getDescription());

		quiz.setRandomOrder(false);
		assertFalse(quiz.isRandomOrder());

		quiz.setOnePage(true);
		assertTrue(quiz.isOnePage());

		quiz.setImmediateCorrection(false);
		assertFalse(quiz.isImmediateCorrection());

		quiz.setPracticeEnabled(false);
		assertFalse(quiz.isPracticeEnabled());
	}
}