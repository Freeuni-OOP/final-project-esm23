package com.quizwebsite.dao;

import com.quizwebsite.model.Answer;

import java.util.List;

public interface AnswerDao {
	List<Answer> findByQuestionId(long questionId);
	Answer insert(Answer answer);
	void delete(long id);
}
