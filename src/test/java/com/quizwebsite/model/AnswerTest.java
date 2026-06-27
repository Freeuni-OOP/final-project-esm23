package com.quizwebsite.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AnswerTest {

	@Test
	public void testConstructorSetsFields() {
		Answer answer = new Answer(99, 99, "USA", 0);
		assertEquals(99, answer.getId());
		assertEquals(99, answer.getQuestionId());
		assertEquals("USA", answer.getAnswerText());
		assertEquals(0, answer.getSlotIndex());
	}

	@Test
	public void testSlotIndexNullSingleAnswer() {
		Answer answer = new Answer(1L, 2L, "Paris", null);
		assertNull(answer.getSlotIndex());
	}

	@Test
	public void testGetAnswerText() {
		Answer answer = new Answer(1L, 2L, "Lebron", null);
		assertEquals("Lebron", answer.getAnswerText());
	}
}