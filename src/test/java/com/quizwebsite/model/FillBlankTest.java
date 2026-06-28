package com.quizwebsite.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FillBlankTest {

	private FillBlank q;
	private List<Answer> answers;

	@BeforeEach
	public void setUp() {
		q = new FillBlank(100, 100, "The capital of France is ________.", 0);
		answers = List.of(new Answer(100, 100, "Paris", null));
	}

	@Test
	public void testGetType() {
		assertEquals(QuestionType.FILL_BLANK, q.getType());
	}

	@Test
	public void testImageUrlIsNull() {
		assertNull(q.getImageUrl());
	}

	@Test
	public void testGrade() {
		assertEquals(1, q.grade(List.of("Paris"), answers));
		assertEquals(1, q.grade(List.of("paris"), answers));
		assertEquals(1, q.grade(List.of("  Paris  "), answers));
		assertEquals(0, q.grade(List.of("Tbilisi"), answers));
	}
}