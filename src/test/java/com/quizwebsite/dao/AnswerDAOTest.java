package com.quizwebsite.dao;

import com.quizwebsite.model.*;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AnswerDAOTest {

	private static AnswerDAO dao;
	private static UserDAO userDAO;
	private static QuizDAO quizDAO;
	private static QuestionDAO questionDAO;

	private long userId;
	private long questionId;

	@BeforeAll
	static void initDao() {
		dao = new AnswerDAO();
		userDAO = new UserDAO();
		quizDAO = new QuizDAO();
		questionDAO = new QuestionDAO();
	}

	@BeforeEach
	void setUp() throws SQLException {
		userId = userDAO.insert(new User("a_tester", "hash", "salt"));
		long quizId = quizDAO.insert(new Quiz(userId, "Test Quiz", null, false,
						true, false, false));
		questionId = questionDAO.insert(new QuestionResponse(0, quizId, "Who won?", 0));
	}

	@AfterEach
	void tearDown() throws SQLException {
		userDAO.delete(userId);
	}

	@Test
	void testInsertAndFind() throws SQLException {
		long id = dao.insert(new Answer(0, questionId, "Italy", null));
		Answer found = dao.findByQuestion(questionId).getFirst();

		assertTrue(id > 0);
		assertEquals("Italy", found.getAnswerText());
		assertEquals(questionId, found.getQuestionId());
		assertNull(found.getSlotIndex()); // null slot must survive round trip
	}

	@Test
	void testInsertKeepsSlotIndex() throws SQLException {
		dao.insert(new Answer(0, questionId, "Rome", 0));
		Answer found = dao.findByQuestion(questionId).get(0);
		assertEquals(0, found.getSlotIndex());
	}

	@Test
	void testFindEmpty() throws SQLException {
		assertTrue(dao.findByQuestion(questionId).isEmpty());
	}

	@Test
	void testFindOrderedBySlot() throws SQLException {
		dao.insert(new Answer(0, questionId, "Third", 2));
		dao.insert(new Answer(0, questionId, "First", 0));
		dao.insert(new Answer(0, questionId, "Second", 1));

		List<Answer> answers = dao.findByQuestion(questionId);
		assertEquals("First", answers.get(0).getAnswerText());
		assertEquals("Second", answers.get(1).getAnswerText());
		assertEquals("Third", answers.get(2).getAnswerText());
	}

	@Test
	void testDelete() throws SQLException {
		dao.insert(new Answer(0, questionId, "Italy", null));
		dao.insert(new Answer(0, questionId, "France", null));

		dao.deleteByQuestion(questionId);
		assertTrue(dao.findByQuestion(questionId).isEmpty());
	}
}