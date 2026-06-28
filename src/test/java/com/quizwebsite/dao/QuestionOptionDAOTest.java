package com.quizwebsite.dao;

import com.quizwebsite.model.*;
import com.quizwebsite.util.DBConnection;
import org.junit.jupiter.api.*;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

public class QuestionOptionDAOTest {

	private QuestionOptionDAO optionDao;
	private QuestionDAO questionDao;
	private long quizId;
	private long questionId;

	@BeforeEach
	public void setUp() throws SQLException {
		optionDao = new QuestionOptionDAO();
		questionDao = new QuestionDAO();
		UserDAO userDao = new UserDAO();
		QuizDAO quizDao = new QuizDAO();

		long userId = userDao.insert(new User("hamilton", "hash", "salt"));
		quizId = quizDao.insert(new Quiz(userId, "F1 Quiz", "Formula 1", false, true, false, false));
		questionId = questionDao.insert(new MultipleChoice(0, quizId, "Who won the 2020 WC?", 0));
	}

	@AfterEach
	public void tearDown() throws SQLException {
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE username = ?")) {
			ps.setString(1, "hamilton");
			ps.executeUpdate();
		}
	}

	@Test
	public void testInsertAndFind() throws SQLException {
		long id = optionDao.insert(new QuestionOption(0, questionId, "Lewis Hamilton", true));

		QuestionOption opt = optionDao.findByQuestion(questionId).get(0);
		assertTrue(id > 0);
		assertEquals("Lewis Hamilton", opt.getOptionText());
		assertEquals(questionId, opt.getQuestionId());
		assertTrue(opt.isCorrect());
	}

	@Test
	public void testFindEmpty() throws SQLException {
		assertTrue(optionDao.findByQuestion(questionId).isEmpty());
	}

	@Test
	public void testDelete() throws SQLException {
		optionDao.insert(new QuestionOption(0, questionId, "Hamilton", true));
		optionDao.insert(new QuestionOption(0, questionId, "Verstappen", false));

		optionDao.deleteByQuestion(questionId);
		assertTrue(optionDao.findByQuestion(questionId).isEmpty());
	}

	@Test
	public void testDeleteOnlyTargetQuestion() throws SQLException {
		long otherId = questionDao.insert(new MultipleChoice(0, quizId, "best F1 team?", 1));
		optionDao.insert(new QuestionOption(0, questionId, "Hamilton", true));
		optionDao.insert(new QuestionOption(0, otherId, "Ferrari", false));

		optionDao.deleteByQuestion(questionId);

		assertTrue(optionDao.findByQuestion(questionId).isEmpty());
		assertEquals(1, optionDao.findByQuestion(otherId).size()); // other untouched
	}
}