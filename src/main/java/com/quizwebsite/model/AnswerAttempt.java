package com.quizwebsite.model;

public class AnswerAttempt {
	private final long id;
	private final long attemptId;
	private final long questionId;
	private final String responseText;
	private final boolean isCorrect;

	// For DB reads
	public AnswerAttempt(long id, long attemptId, long questionId, String responseText, boolean isCorrect) {
		this.id = id;
		this.attemptId = attemptId;
		this.questionId = questionId;
		this.responseText = responseText;
		this.isCorrect = isCorrect;
	}

	// For new inserts
	public AnswerAttempt(long attemptId, long questionId, String responseText, boolean isCorrect) {
		this.id = 0;
		this.attemptId = attemptId;
		this.questionId = questionId;
		this.responseText = responseText;
		this.isCorrect = isCorrect;
	}

	public long getId() { return id; }
	public long getAttemptId() { return attemptId; }
	public long getQuestionId() { return questionId; }
	public String getResponseText() { return responseText; }
	public boolean isCorrect() { return isCorrect; }
}