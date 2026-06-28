package com.quizwebsite.model;

public class Answer {
	private final long id;
	private final long questionId;
	private final String answerText;
	private final Integer slotIndex;

	public Answer(long id, long questionId, String answerText, Integer slotIndex) {
		this.id = id;
		this.questionId = questionId;
		this.answerText = answerText;
		this.slotIndex = slotIndex;
	}

	public long getId() { return id; }
	public long getQuestionId() { return questionId; }
	public String getAnswerText() { return answerText; }
	public Integer getSlotIndex() { return slotIndex; }
}