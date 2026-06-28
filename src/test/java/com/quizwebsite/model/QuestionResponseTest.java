package com.quizwebsite.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class QuestionResponseTest {

	private QuestionResponse q;
	private List<Answer> answers;

	@BeforeEach
	public void setUp() {
		q = new QuestionResponse(1L, 1L, "Vin iyo Sakartvelos pirveli prezidenti?", 0);
		answers = List.of(
						new Answer(1L, 1L, "Gamsakhurdia", null),
						new Answer(2L, 1L, "Zviad Gamsakhurdia", null)
		);
	}

	@Test
	public void testGetType() {
		assertEquals(QuestionType.QUESTION_RESPONSE, q.getType());
	}

	@Test
	public void testGetQuestionText() {
		assertEquals("Vin iyo Sakartvelos pirveli prezidenti?", q.getQuestionText());
	}

	@Test
	public void testImageUrlIsNull() {
		assertNull(q.getImageUrl());
	}

	@Test
	public void testGrade() {
		assertEquals(1, q.grade(List.of("Gamsakhurdia"), answers));
		assertEquals(1, q.grade(List.of("gamsakhurdia"), answers));
		assertEquals(1, q.grade(List.of("Zviad Gamsakhurdia"), answers));
		assertEquals(0, q.grade(List.of("Eduard Shevardnadze"), answers));
	}
}