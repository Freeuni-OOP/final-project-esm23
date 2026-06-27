package com.quizwebsite.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class QuizAttemptTest {

	@Test
	public void testFullConstructorSetsAllFields() {
		LocalDateTime now = LocalDateTime.now();
		QuizAttempt attempt = new QuizAttempt(1, 2, 3, 8, 10, 120, false, now);

		assertEquals(1, attempt.getId());
		assertEquals(2, attempt.getUserId());
		assertEquals(3, attempt.getQuizId());
		assertEquals(8, attempt.getScore());
		assertEquals(10, attempt.getMaxScore());
		assertEquals(120, attempt.getTimeTakenSeconds());
		assertFalse(attempt.isPractice());
		assertEquals(now, attempt.getTakenAt());
	}

	@Test
	public void testInsertConstructorDefaults() {
		QuizAttempt attempt = new QuizAttempt(2, 3, 8, 10, 120, false);

		assertEquals(0, attempt.getId());
		assertNull(attempt.getTakenAt());
	}

	@Test
	public void testPracticeModeAttempt() {
		QuizAttempt attempt = new QuizAttempt(2, 3, 5, 10, 60, true);
		assertTrue(attempt.isPractice());
	}

	@Test
	public void testScoreAndMaxScore() {
		QuizAttempt attempt = new QuizAttempt(2, 3, 7, 10, 90, false);
		assertEquals(7, attempt.getScore());
		assertEquals(10, attempt.getMaxScore());
	}
}