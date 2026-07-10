package com.quizwebsite.service;

// answer reviews
// multi-answer scoring
public record QuestionOutcome(long questionId, int earned, int maxPoints) {}