package com.quizwebsite.service;

import java.util.List;

// shows score/maxScore and timeTakenSeconds right after submit.
// lets the servlets redirect to a permanent results url
// practice mode: attemptId = 0 signals nothing was tried, so the page knows shows the score without history.

public record ScoringResult(long attemptId, int score, int maxScore, int timeTakenSeconds, List<QuestionOutcome> outcomes) {}