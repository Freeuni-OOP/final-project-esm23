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

    //tracking points explicitly handles grading variations like Multi-Answer or fill-in-the-Blank questions, where a single question block can award multiple points//
    private final int earnedPoints;
    private final int maxPoints;


    //constructs a fully graded review row for an individual question//
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

   //returns the question prompt text, or the external target URL if the source type is a Picture-Response//
    public long getQuestionId() { return questionId; }

    public String getQuestionText() { return questionText; }

    public String getUserResponse() { return userResponse; }

    public boolean isCorrect() { return correct; }

    //returns thee expected correct answer. For multi-answer or alternative option questions, this should return a clean, user-readable string of the valid option(s)//
    public String getCorrectAnswerText() { return correctAnswerText; }

    public int getEarnedPoints() { return earnedPoints; }

    public int getMaxPoints() { return maxPoints; }
}