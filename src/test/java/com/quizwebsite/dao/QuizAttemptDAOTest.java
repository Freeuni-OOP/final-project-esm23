package com.quizwebsite.dao;

import com.quizwebsite.model.Quiz;
import com.quizwebsite.model.QuizAttempt;
import com.quizwebsite.model.User;
import org.junit.jupiter.api.*;

import java.util.List;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class QuizAttemptDAOTest {

	private static QuizAttemptDAO dao;
	private static UserDAO userDAO;
	private static QuizDAO quizDAO;

	private long userId;
	private long quizId;

	@BeforeAll
	static void initDao() {
		dao = new QuizAttemptDAO();
		userDAO = new UserDAO();
		quizDAO = new QuizDAO();
	}

	@BeforeEach
	void setUp() throws SQLException {
		userId = userDAO.insert(new User("attempt_tester", "hash", "salt"));
		quizId = quizDAO.insert(new Quiz(userId, "Test Quiz", null, false, true, false, false));
	}

	@AfterEach
	void tearDown() throws SQLException {
		userDAO.delete(userId);
	}

	@Test
	void testInsertAndFind() throws SQLException {
		long id = dao.insert(new QuizAttempt(userId, quizId, 8, 10, 120, false));
		assertTrue(id > 0);
		List<QuizAttempt> list = dao.findByUserAndQuiz(userId, quizId);
		assertEquals(1, list.size());
		QuizAttempt a = list.get(0);
		assertEquals(8, a.getScore());
		assertEquals(10, a.getMaxScore());
		assertEquals(120, a.getTimeTakenSeconds());
		assertFalse(a.isPractice());
	}

	@Test
	void testFindByUserAndQuizEmpty() throws SQLException {
		assertTrue(dao.findByUserAndQuiz(userId, quizId).isEmpty());
	}

	@Test
	void testFindByUserAcrossQuizzes() throws SQLException {
		long quiz2 = quizDAO.insert(new Quiz(userId, "Quiz 2", null, false, true, false, false));
		dao.insert(new QuizAttempt(userId, quizId, 5, 10, 60, false));
		dao.insert(new QuizAttempt(userId, quiz2, 9, 10, 90, false));
		assertEquals(2, dao.findByUser(userId).size());
	}

	@Test
	void testTopScoresRanking() throws SQLException {
		// score desc-> then time asc on ties; insert order is wrong on purpose
		dao.insert(new QuizAttempt(userId, quizId, 7, 10, 300, false));
		dao.insert(new QuizAttempt(userId, quizId, 7, 10, 100, false));
		dao.insert(new QuizAttempt(userId, quizId, 9, 10, 200, false));
		List<QuizAttempt> top = dao.findTopScores(quizId, 10);
		assertEquals(9, top.get(0).getScore());
		assertEquals(100, top.get(1).getTimeTakenSeconds());
		assertEquals(300, top.get(2).getTimeTakenSeconds());
	}

	@Test
	void testCountAllExcludesPractice() throws SQLException {
		int before = dao.countAll();
		dao.insert(new QuizAttempt(userId, quizId, 4, 10, 60, false));
		dao.insert(new QuizAttempt(userId, quizId, 4, 10, 60, true)); // practice is not counted
		assertEquals(before + 1, dao.countAll());
	}
}