package com.quizwebsite.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PictureResponseTest {

	private PictureResponse question;
	private List<Answer> answers;

	@BeforeEach
	void setUp() {
		question = new PictureResponse(
						1, 10,
						"What footballer is shown?",
						"https://example.com/messi.jpg",
						0
		);
		answers = List.of(
						new Answer(1, 1, "Messi", null),
						new Answer(1, 1, "Lionel Messi", null)
		);
	}

	@Test
	void correctAnswerExactMatch() {
		assertTrue(question.checkAnswer("messi", answers));
	}

	@Test
	void correctAnswerCaseInsensitive() {
		assertTrue(question.checkAnswer("MESSI", answers));
	}

	@Test
	void correctAnswerTrimsWhitespace() {
		assertTrue(question.checkAnswer("  messi  ", answers));
	}

	@Test
	void secondAcceptedAnswerWorks() {
		assertTrue(question.checkAnswer("Lionel Messi", answers));
	}

	@Test
	void wrongAnswerReturnsFalse() {
		assertFalse(question.checkAnswer("Ronaldo", answers));
	}

	@Test
	void nullAnswerListReturnsFalse() {
		assertFalse(question.checkAnswer("Messi", null));
	}

	@Test
	void nullInputReturnsFalse() {
		assertFalse(question.checkAnswer(null, answers));
	}

	@Test
	void typeIsPictureResponse() {
		assertEquals(QuestionType.PICTURE_RESPONSE, question.getType());
	}

	@Test
	void imageUrlIsSet() {
		assertEquals("https://example.com/messi.jpg", question.getImageUrl());
	}
}