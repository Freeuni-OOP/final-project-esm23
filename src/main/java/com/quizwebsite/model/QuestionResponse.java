package com.quizwebsite.model;

import java.util.List;

public class QuestionResponse extends Question {

	public QuestionResponse(long id, long quizId, String questionText, int position) {
		super(id, quizId, QuestionType.QUESTION_RESPONSE, questionText, null, position);
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