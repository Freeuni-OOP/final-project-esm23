package com.quizwebsite.service;

import com.quizwebsite.dao.*;
import com.quizwebsite.model.*;
import java.sql.SQLException;
import java.util.*;

public class QuizScoringService {
	private final QuestionDAO questionDAO;
	private final AnswerDAO answerDAO;
	private final QuizAttemptDAO attemptDAO;
	private final AnswerAttemptDAO answerAttemptDAO;
	private final QuestionOptionDAO optionDAO;

	public QuizScoringService(QuestionDAO q, AnswerDAO a, QuizAttemptDAO at, AnswerAttemptDAO aa, QuestionOptionDAO o) {
		this.questionDAO = q;
		this.answerDAO = a;
		this.attemptDAO = at;
		this.answerAttemptDAO = aa;
		this.optionDAO = o;
	}

	// grades all questions, persists attempt + per-answer rows, return the result
	public ScoringResult score(long userId, long quizId, Map<Long, List<String>> responses,
														 int timeTakenSeconds, boolean isPractice) throws SQLException {
		List<Question> questions = questionDAO.findByQuiz(quizId);
		int total = 0;
		int max = 0;
		List<QuestionOutcome> outcomes = new ArrayList<>();

		for (Question question : questions) {
			List<Answer> correct = answerDAO.findByQuestion(question.getId());

			// mc needs its options loaded before grading
			if (question instanceof MultipleChoice mc) {
				mc.setOptions(optionDAO.findByQuestion(question.getId()));
			}

			List<String> userResp = responses.getOrDefault(question.getId(), List.of());
			int earned = question.grade(userResp, correct);
			int points = question.maxPoints(correct);

			total += earned;
			max += points;
			outcomes.add(new QuestionOutcome(question.getId(), earned, points));
		}

		// practice attempts are graded but score isn't recorded
		if (isPractice) {
			return new ScoringResult(0, total, max, timeTakenSeconds, outcomes);
		}

		// if not practice, record the attempt.
		long attemptId = attemptDAO.insert(
						new QuizAttempt(userId, quizId, total, max, timeTakenSeconds, false));

		// loop questions, not responses, so skipped ones still get a row
		for (Question question : questions) {
			long qId = question.getId();
			List<String> resp = responses.getOrDefault(qId, List.of());
			String joined = resp.isEmpty() ? null : String.join(" | ", resp); // null marks a skip
			int earned = earnedFor(outcomes, qId);
			answerAttemptDAO.insert(new AnswerAttempt(attemptId, qId, joined, earned > 0));
		}

		return new ScoringResult(attemptId, total, max, timeTakenSeconds, outcomes);
	}

	// find how many points a question scored, by id, from the outcomes list
	// built during grading. returns 0 if not found (skipped or unknown id).
	private int earnedFor(List<QuestionOutcome> outcomes, long questionId) {
		for (QuestionOutcome o : outcomes) {
			if (o.questionId() == questionId) return o.earned();
		}
		return 0;
	}
}