package com.quizwebsite.model;

import java.util.List;

public class PictureResponse extends Question {

	public PictureResponse(long id, long quizId, String questionText, String imageUrl, int position) {
		super(id, quizId, QuestionType.PICTURE_RESPONSE, questionText, imageUrl, position);
	}

	@Override
	public boolean checkAnswer(String userInput, List<Answer> correctAnswers) {
		if (userInput == null || correctAnswers == null) return false;
		String normalized = userInput.trim().toLowerCase();
		for (Answer answer : correctAnswers) {
			if (answer.getAnswerText().trim().toLowerCase().equals(normalized)) {
				return true;
			}
		}
		return false;
	}
}