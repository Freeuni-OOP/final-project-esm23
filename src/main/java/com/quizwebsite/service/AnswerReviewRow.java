package com.quizwebsite.service;

/**
 * One row of the post-quiz review: a single question, what the user
 * answered, whether it was graded correct, and what the correct answer
 * actually was. Built by TakeQuizServlet after scoring so quizResults.jsp
 * has everything it needs without re-querying the DB per row.
 */
public class AnswerReviewRow {
    private final long questionId;
    private final String questionText;
    private final String userResponse;
    private final boolean correct;
    private final String correctAnswerText;

    //tracks points explicitly to handle varying question point maximums,
    //such as Multi-Answer questions where each blank counts for a separate point//
    private final int earnedPoints;
    private final int maxPoints;

    //constructs a comprehensive review row instance for a single quiz question//
    public AnswerReviewRow(long questionId, String questionText, String userResponse,
                           boolean correct, String correctAnswerText,
                           int earnedPoints, int maxPoints) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.userResponse = userResponse;
        this.correct = correct;
        this.correctAnswerText = correctAnswerText;
        this.earnedPoints = earnedPoints;
        this.maxPoints = maxPoints;
    }

    public long getQuestionId() { return questionId; }
    public String getQuestionText() { return questionText; }
    public String getUserResponse() { return userResponse; }
    public boolean isCorrect() { return correct; }
    public String getCorrectAnswerText() { return correctAnswerText; }
    public int getEarnedPoints() { return earnedPoints; }
    public int getMaxPoints() { return maxPoints; }
}