package com.quizwebsite.model;

import java.time.LocalDateTime;

public class QuizAttempt {
	private final long id;
	private final long userId;
	private final long quizId;
	private final int score;
	private final int maxScore;
	private final int timeTakenSeconds;
	private final boolean isPractice;
	private final LocalDateTime takenAt;

	// For DB reads
	public QuizAttempt(long id, long userId, long quizId, int score, int maxScore, int timeTakenSeconds, boolean isPractice, LocalDateTime takenAt) {
		this.id = id;
		this.userId = userId;
		this.quizId = quizId;
		this.score = score;
		this.maxScore = maxScore;
		this.timeTakenSeconds = timeTakenSeconds;
		this.isPractice = isPractice;
		this.takenAt = takenAt;
	}

	// For new attempt inserts
	public QuizAttempt(long userId, long quizId, int score, int maxScore, int timeTakenSeconds, boolean isPractice) {
		this.id = 0;
		this.userId = userId;
		this.quizId = quizId;
		this.score = score;
		this.maxScore = maxScore;
		this.timeTakenSeconds = timeTakenSeconds;
		this.isPractice = isPractice;
		this.takenAt = null;
	}

	public long getId() { return id; }
	public long getUserId() { return userId; }
	public long getQuizId() { return quizId; }
	public int getScore() { return score; }
	public int getMaxScore() { return maxScore; }
	public int getTimeTakenSeconds() { return timeTakenSeconds; }
	public boolean isPractice() { return isPractice; }
	public LocalDateTime getTakenAt() { return takenAt; }
}