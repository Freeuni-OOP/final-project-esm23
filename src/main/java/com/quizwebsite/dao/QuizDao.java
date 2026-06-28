package com.quizwebsite.dao;

import com.quizwebsite.model.Quiz;

import java.util.List;
import java.util.Optional;

public interface QuizDao {
	Optional<Quiz> findById(long id);
	List<Quiz> findByCreator(long creatorId);
	List<Quiz> findAll();
	Quiz insert(Quiz quiz);
	void update(Quiz quiz);
	void delete(long id);
}
