package com.quizwebsite.model;

import java.util.List;

public abstract class Question {
	private final long id;
	private final long quizId;
	private final QuestionType type;
	private final String questionText;
	private final String imageUrl;
	private int position;
	private List<Answer> correctAnswers;

	public Question(long id, long quizId, QuestionType type, String questionText, String imageUrl, int position) {
		this.id = id;
		this.quizId = quizId;
		this.type = type;
		this.questionText = questionText;
		this.imageUrl = imageUrl;
		this.position = position;
	}

	public abstract int grade(List<String> responses, List<Answer> correctAnswers);
	public abstract int maxPoints(List<Answer> correctAnswers); // how much each question is worth

	public long getId() { return id; }
	public long getQuizId() { return quizId; }
	public QuestionType getType() { return type; }
	public String getQuestionText() { return questionText; }
	public String getImageUrl() { return imageUrl; }
	public int getPosition() { return position; }

	public void setPosition(int position) { this.position = position; }

	public List<Answer> getCorrectAnswers() { return correctAnswers; }
	public void setCorrectAnswers(List<Answer> correctAnswers) { this.correctAnswers = correctAnswers; }
}