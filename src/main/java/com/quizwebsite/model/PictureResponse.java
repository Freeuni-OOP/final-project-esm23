package com.quizwebsite.model;

import java.util.List;

public class PictureResponse extends Question {

	public PictureResponse(long id, long quizId, String questionText, String imageUrl, int position) {
		super(id, quizId, QuestionType.PICTURE_RESPONSE, questionText, imageUrl, position);
	}

	@Override
	public int grade(List<String> responses, List<Answer> correctAnswers) {
		if (responses == null || responses.isEmpty() || correctAnswers == null) return 0;
		String input = responses.get(0).trim().toLowerCase();
		for (Answer a : correctAnswers) {
			if (a.getAnswerText().trim().toLowerCase().equals(input)) return 1;
		}
		return 0;
	}
	@Override
	public int maxPoints(List<Answer> correctAnswers) { return 1; }
}