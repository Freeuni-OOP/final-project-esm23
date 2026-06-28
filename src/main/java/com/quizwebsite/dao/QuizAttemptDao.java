package com.quizwebsite.dao;

import com.quizwebsite.model.QuizAttempt;

import java.util.List;
import java.util.Optional;

public interface QuizAttemptDao {
	Optional<QuizAttempt> findById(long id);
	List<QuizAttempt> findByUser(long userId);
	List<QuizAttempt> findByQuiz(long quizId);
	QuizAttempt insert(QuizAttempt attempt);
}
