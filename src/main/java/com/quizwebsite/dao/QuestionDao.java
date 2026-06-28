package com.quizwebsite.dao;

import com.quizwebsite.model.Question;

import java.util.List;
import java.util.Optional;

public interface QuestionDao {
	Optional<Question> findById(long id);
	List<Question> findByQuizId(long quizId);
	Question insert(Question question);
	void delete(long id);
}
