package com.quizwebsite.model;

import java.util.List;

public class FillBlank extends Question {

	public FillBlank(long id, long quizId, String questionText, int position) {
		super(id, quizId, QuestionType.FILL_BLANK, questionText, null, position);
	}

	@Override
	public boolean checkAnswer(String userInput, List<Answer> correctAnswers) {
		for (Answer answer : correctAnswers) {
			if (answer.getAnswerText().equalsIgnoreCase(userInput.trim())) {
				return true;
			}
		}
		return false;
	}
}