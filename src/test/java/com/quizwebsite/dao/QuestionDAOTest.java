package com.quizwebsite.dao;

import com.quizwebsite.model.*;
import com.quizwebsite.util.DBConnection;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QuestionDAOTest {

	private static QuestionDAO dao;
	private static UserDAO userDAO;
	private static QuizDAO quizDAO;

	private long userId;
	private long quizId;

	@BeforeAll
	static void initDao() {
		dao = new QuestionDAO();
		userDAO = new UserDAO();
		quizDAO = new QuizDAO();
	}

	@BeforeEach
	void setUp() throws SQLException {
		userId = userDAO.insert(new User("q_tester", "hash", "salt"));
		quizId = quizDAO.insert(new Quiz(userId, "Test Quiz", null, false, true, false, false));
	}

	@AfterEach
	void tearDown() throws SQLException {
		// cascade deletes questions
		userDAO.delete(userId);
	}

	// findById

	@Test
	void testFindByIdReturnsNull() throws SQLException {
		assertNull(dao.findById(999999));
	}

	@Test
	void testFindByIdReturnsQuestion() throws SQLException {
		Question q = new QuestionResponse(0, quizId, "Who won the 2006 WC?", 0);
		long id = dao.insert(q);
		Question found = dao.findById(id);
		assertNotNull(found);
		assertEquals("Who won the 2006 WC?", found.getQuestionText());
		assertEquals(QuestionType.QUESTION_RESPONSE, found.getType());
	}

	//findByQuiz

	@Test
	void testFindByQuizEmptyList() throws SQLException {
		List<Question> questions = dao.findByQuiz(quizId);
		assertTrue(questions.isEmpty());
	}

	@Test
	void testFindByQuizReturnsAll() throws SQLException {
		dao.insert(new QuestionResponse(0, quizId, "Q1", 0));
		dao.insert(new FillBlank(0, quizId, "The capital of Italy is ________.", 1));
		dao.insert(new MultipleChoice(0, quizId, "Who scored in the final?", 2));

		List<Question> questions = dao.findByQuiz(quizId);
		assertEquals(3, questions.size());
	}

	@Test
	void testFindByQuizOrderedByPosition() throws SQLException {
		// insert out of order deliberately
		dao.insert(new QuestionResponse(0, quizId, "Third", 2));
		dao.insert(new QuestionResponse(0, quizId, "First", 0));
		dao.insert(new QuestionResponse(0, quizId, "Second", 1));

		List<Question> questions = dao.findByQuiz(quizId);
		assertEquals("First", questions.get(0).getQuestionText());
		assertEquals("Second", questions.get(1).getQuestionText());
		assertEquals("Third", questions.get(2).getQuestionText());
	}
}