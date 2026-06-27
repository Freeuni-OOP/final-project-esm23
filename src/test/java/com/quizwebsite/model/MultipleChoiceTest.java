package com.quizwebsite.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MultipleChoiceTest {

	private MultipleChoice q;

	@BeforeEach
	public void setUp() {
		q = new MultipleChoice(1, 1, "Who won the 2020 F1 WC?", 0);
		q.setOptions(List.of(
						new QuestionOption(1, 1, "Lewis Hamilton", true),
						new QuestionOption(2, 1, "Max Verstappen", false),
						new QuestionOption(3, 1, "Valtteri Bottas", false)
		));
	}

	@Test
	public void testGetType() {
		assertEquals(QuestionType.MULTIPLE_CHOICE, q.getType());
	}

	@Test
	public void testImageUrlIsNull() {
		assertNull(q.getImageUrl());
	}

	@Test
	public void testSetAndGetOptions() {
		assertEquals(3, q.getOptions().size());
		assertEquals("Lewis Hamilton", q.getOptions().get(0).getOptionText());
	}

	@Test
	public void testCheckAnswer() {
		assertTrue(q.checkAnswer("Lewis Hamilton", null));
		assertTrue(q.checkAnswer("lewis hamilton", null));
		assertTrue(q.checkAnswer("  Lewis Hamilton  ", null));
		assertFalse(q.checkAnswer("Max Verstappen", null));
	}
}