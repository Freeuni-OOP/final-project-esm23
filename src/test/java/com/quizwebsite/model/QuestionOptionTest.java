package com.quizwebsite.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QuestionOptionTest {

	@Test
	public void testConstructorSetsAllFields() {
		QuestionOption option = new QuestionOption(1, 10, "Lewis Hamilton", true);
		assertEquals(1, option.getId());
		assertEquals(10, option.getQuestionId());
		assertEquals("Lewis Hamilton", option.getOptionText());
		assertTrue(option.isCorrect());
	}

	@Test
	public void testIncorrectOption() {
		QuestionOption option = new QuestionOption(2, 10, "Max Verstappen", false);
		assertFalse(option.isCorrect());
	}

	@Test
	public void testGetOptionText() {
		QuestionOption option = new QuestionOption(3, 10, "Sebastian Vettel", false);
		assertEquals("Sebastian Vettel", option.getOptionText());
	}
}