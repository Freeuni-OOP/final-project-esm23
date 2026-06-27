package com.quizwebsite.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QuestionFactoryTest {

	@Test
	public void testCreatesCorrectTypes() {
		assertInstanceOf(QuestionResponse.class, QuestionFactory.create(QuestionType.QUESTION_RESPONSE, 1, 1, "Q", null, 0));
		assertInstanceOf(FillBlank.class, QuestionFactory.create(QuestionType.FILL_BLANK, 1, 1, "Q", null, 0));
		assertInstanceOf(MultipleChoice.class, QuestionFactory.create(QuestionType.MULTIPLE_CHOICE, 1, 1, "Q", null, 0));
		assertInstanceOf(PictureResponse.class, QuestionFactory.create(QuestionType.PICTURE_RESPONSE, 1, 1, "Q", "http://example.com/img.jpg", 0));
	}

	@Test
	public void testPreservesFields() {
		Question q = QuestionFactory.create(QuestionType.PICTURE_RESPONSE, 1, 1, "Which car is this?", "http://example.com/car.jpg", 3);
		assertEquals("Which car is this?", q.getQuestionText());
		assertEquals("http://example.com/car.jpg", q.getImageUrl());
		assertEquals(3, q.getPosition());
	}
}