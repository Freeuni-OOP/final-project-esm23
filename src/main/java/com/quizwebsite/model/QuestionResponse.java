package com.quizwebsite.model;

import java.util.List;

public class QuestionResponse extends Question {

	public QuestionResponse(long id, long quizId, String questionText, int position) {
		super(id, quizId, QuestionType.QUESTION_RESPONSE, questionText, null, position);
	}

	@Override
	public int grade(List<String> responses, List<Answer> correctAnswers) {
		if (responses == null || responses.isEmpty()) return 0;
		String input = responses.get(0).trim();
		for (Answer a : correctAnswers) {
			if (a.getAnswerText().equalsIgnoreCase(input)) return 1;
		}
		return 0;
	}

	@Override
	public int maxPoints(List<Answer> correctAnswers) {
		return 1;
	}

}