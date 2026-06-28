package com.quizwebsite.dao;

import com.quizwebsite.model.AnswerAttempt;

import java.util.List;

public interface AnswerAttemptDao {
	List<AnswerAttempt> findByAttemptId(long attemptId);
	AnswerAttempt insert(AnswerAttempt answerAttempt);
}
