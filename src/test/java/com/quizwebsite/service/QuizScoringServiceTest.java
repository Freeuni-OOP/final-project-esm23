package com.quizwebsite.service;

import com.quizwebsite.dao.*;
import com.quizwebsite.model.*;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class QuizScoringServiceTest {

	private QuizScoringService service;
	private UserDAO userDAO;
	private QuestionDAO questionDAO;
	private AnswerDAO answerDAO;
	private QuizAttemptDAO attemptDAO;
	private long userId;
	private long quizId;

	@BeforeEach
	void setUp() throws SQLException {
		userDAO = new UserDAO();
		questionDAO = new QuestionDAO();
		answerDAO = new AnswerDAO();
		attemptDAO = new QuizAttemptDAO();
		service = new QuizScoringService(questionDAO, answerDAO, attemptDAO,
						new AnswerAttemptDAO(), new QuestionOptionDAO());

		userId = userDAO.insert(new User("scorer", "h", "s"));
		quizId = new QuizDAO().insert(new Quiz(userId, "F1 Quiz", null, false, true, false, false));
	}

	@AfterEach
	void tearDown() throws SQLException {
		userDAO.delete(userId); // cascade clears the rest
	}

	private long addQuestion(String text, String answer, int pos) throws SQLException {
		long q = questionDAO.insert(new QuestionResponse(0, quizId, text, pos));
		answerDAO.insert(new Answer(0, q, answer, null));
		return q;
	}

	@Test
	void testScoresMixedQuiz() throws SQLException {
		long q1 = addQuestion("2020 WC?", "Hamilton", 0);
		long q2 = addQuestion("2021 WC?", "Verstappen", 1);
		Map<Long, List<String>> resp = Map.of(q1, List.of("Hamilton"), q2, List.of("Bottas"));

		ScoringResult r = service.score(userId, quizId, resp, 90, false);
		assertEquals(1, r.score());
		assertEquals(2, r.maxScore());
		assertTrue(r.attemptId() > 0);
	}

	@Test
	void testPracticeNotPersisted() throws SQLException {
		long q = addQuestion("2020 WC?", "Hamilton", 0);
		ScoringResult r = service.score(userId, quizId, Map.of(q, List.of("Hamilton")), 140, true);
		assertEquals(0, r.attemptId());
		assertTrue(attemptDAO.findByUserAndQuiz(userId, quizId).isEmpty());
	}
}