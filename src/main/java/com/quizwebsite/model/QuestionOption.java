package com.quizwebsite.model;


/**
 * Represents a single answer option for a multiple choice question.
 * Maps to one row in the question_options table.
 */
public class QuestionOption {
	private final long id;
	private final long questionId;
	private final String optionText;
	private final boolean isCorrect;

	public QuestionOption(long id, long questionId, String optionText, boolean isCorrect) {
		this.id = id;
		this.questionId = questionId;
		this.optionText = optionText;
		this.isCorrect = isCorrect;
	}

	public long getId() { return id; }
	public long getQuestionId() { return questionId; }
	public String getOptionText() { return optionText; }
	public boolean isCorrect() { return isCorrect; }
}