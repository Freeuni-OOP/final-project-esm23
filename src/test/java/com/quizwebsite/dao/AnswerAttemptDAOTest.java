package com.quizwebsite.dao;

import com.quizwebsite.model.*;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AnswerAttemptDAOTest {

	private static AnswerAttemptDAO dao;
	private static UserDAO userDAO;
	private static QuizDAO quizDAO;
	private static QuestionDAO questionDAO;
	private static QuizAttemptDAO attemptDAO;

	private long userId;
	private long questionId;
	private long attemptId;

	@BeforeAll
	static void initDao() {
		dao = new AnswerAttemptDAO();
		userDAO = new UserDAO();
		quizDAO = new QuizDAO();
		questionDAO = new QuestionDAO();
		attemptDAO = new QuizAttemptDAO();
	}

	@BeforeEach
	void setUp() throws SQLException {
		userId = userDAO.insert(new User("aa_tester", "hash", "salt"));
		long quizId = quizDAO.insert(new Quiz(userId, "Test Quiz", null, false, true, false, false));
		questionId = questionDAO.insert(new QuestionResponse(0, quizId, "Q1", 0));
		attemptId = attemptDAO.insert(new QuizAttempt(userId, quizId, 1, 1, 30, false));
	}

	@AfterEach
	void tearDown() throws SQLException {
		userDAO.delete(userId);
	}

	@Test
	void testInsertAndFind() throws SQLException {
		long id = dao.insert(new AnswerAttempt(attemptId, questionId, "Paris", true));
		assertTrue(id > 0);
		List<AnswerAttempt> list = dao.findByAttempt(attemptId);
		assertEquals(1, list.size());
		AnswerAttempt aa = list.getFirst();
		assertEquals("Paris", aa.getResponseText());
		assertTrue(aa.isCorrect());
	}

	@Test
	void testFindByAttemptEmpty() throws SQLException {
		assertTrue(dao.findByAttempt(attemptId).isEmpty());
	}

	@Test
	void testFindByAttemptReturnsAll() throws SQLException {
		dao.insert(new AnswerAttempt(attemptId, questionId, "a", true));
		dao.insert(new AnswerAttempt(attemptId, questionId, "b", false));
		assertEquals(2, dao.findByAttempt(attemptId).size());
	}

	@Test
	void testNullResponseText() throws SQLException {
		// skipped/blank answers store null
		dao.insert(new AnswerAttempt(attemptId, questionId, null, false));
		assertNull(dao.findByAttempt(attemptId).getFirst().getResponseText());
	}
}