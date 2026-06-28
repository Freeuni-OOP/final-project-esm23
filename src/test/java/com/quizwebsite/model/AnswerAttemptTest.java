package com.quizwebsite.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AnswerAttemptTest {

	@Test
	public void testFullConstructorSetsAllFields() {
		AnswerAttempt aa = new AnswerAttempt(1, 2, 3, "Lewis Hamilton", true);

		assertEquals(1, aa.getId());
		assertEquals(2, aa.getAttemptId());
		assertEquals(3, aa.getQuestionId());
		assertEquals("Lewis Hamilton", aa.getResponseText());
		assertTrue(aa.isCorrect());
	}

	@Test
	public void testInsertConstructorDefaults() {
		AnswerAttempt aa = new AnswerAttempt(2, 3, "Max Verstappen", false);

		assertEquals(0, aa.getId());
		assertFalse(aa.isCorrect());
	}

	@Test
	public void testWrongAnswer() {
		AnswerAttempt aa = new AnswerAttempt(1, 2, 3, "Valtteri Bottas", false);
		assertFalse(aa.isCorrect());
		assertEquals("Valtteri Bottas", aa.getResponseText());
	}
}