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
	void exactMatch() {
		assertEquals(1, question.grade(List.of("messi"), answers));
	}

	@Test
	void caseInsensitive() {
		assertEquals(1, question.grade(List.of("MESSI"), answers));
	}

	@Test
	void trimsWhitespace() {
		assertEquals(1, question.grade(List.of("  messi  "), answers));
	}

	@Test
	void secondAcceptedAnswer() {
		assertEquals(1, question.grade(List.of("Lionel Messi"), answers));
	}

	@Test
	void wrongAnswer() {
		assertEquals(0, question.grade(List.of("Ronaldo"), answers));
	}

	@Test
	void nullAnswerList() {
		assertEquals(0, question.grade(List.of("Messi"), null));
	}

	@Test
	void emptyResponses() {
		assertEquals(0, question.grade(List.of(), answers));
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