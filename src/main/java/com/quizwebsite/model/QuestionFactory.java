package com.quizwebsite.model;

public class QuestionFactory {

	// instead of calling new QuestionResponse() or new MultipleCHoice(), this method is available.

	public static Question create(QuestionType type, long id, long quizId, String questionText, String imageUrl, int position) {
		return switch (type) {
			case QUESTION_RESPONSE -> new QuestionResponse(id, quizId, questionText, position);
			case FILL_BLANK -> new FillBlank(id, quizId, questionText, position);
			case MULTIPLE_CHOICE -> new MultipleChoice(id, quizId, questionText, position);
			case PICTURE_RESPONSE -> new PictureResponse(id, quizId, questionText, imageUrl, position);
		};
	}
}