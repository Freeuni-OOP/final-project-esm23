package com.quizwebsite.dao;

import com.quizwebsite.model.*;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
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
		userDAO.delete(userId); // cascade deletes questions
	}

	@Test
	void testInsertAndFindById() throws SQLException {
		long id = dao.insert(new QuestionResponse(0, quizId, "Who won the 2006 WC?", 0));
		Question found = dao.findById(id);

		assertTrue(id > 0);
		assertEquals("Who won the 2006 WC?", found.getQuestionText());
		assertEquals(QuestionType.QUESTION_RESPONSE, found.getType());
		assertNull(found.getImageUrl());
	}

	@Test
	void testInsertPictureResponseKeepsImageUrl() throws SQLException {
		long id = dao.insert(new PictureResponse(0, quizId, "Which badge?", "https://example.com/badge.png", 0));
		Question found = dao.findById(id);

		assertEquals(QuestionType.PICTURE_RESPONSE, found.getType());
		assertEquals("https://example.com/badge.png", found.getImageUrl());
	}

	@Test
	void testFindByIdReturnsNull() throws SQLException {
		assertNull(dao.findById(999999));
	}

	@Test
	void testFindByQuizEmpty() throws SQLException {
		assertTrue(dao.findByQuiz(quizId).isEmpty());
	}

	@Test
	void testFindByQuizOrderedByPosition() throws SQLException {
		// insert out of order deliberately
		dao.insert(new QuestionResponse(0, quizId, "Third", 2));
		dao.insert(new QuestionResponse(0, quizId, "First", 0));
		dao.insert(new QuestionResponse(0, quizId, "Second", 1));

		List<Question> questions = dao.findByQuiz(quizId);
		assertEquals(3, questions.size());
		assertEquals("First", questions.get(0).getQuestionText());
		assertEquals("Second", questions.get(1).getQuestionText());
		assertEquals("Third", questions.get(2).getQuestionText());
	}

	@Test
	void testDeleteOnlyRemovesTargetRow() throws SQLException {
		long id1 = dao.insert(new QuestionResponse(0, quizId, "Q1", 0));
		long id2 = dao.insert(new QuestionResponse(0, quizId, "Q2", 1));

		dao.delete(id1);
		assertNull(dao.findById(id1));
		assertNotNull(dao.findById(id2)); // second row must survive
	}
}