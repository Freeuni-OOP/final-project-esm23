package com.quizwebsite.dao;

import com.quizwebsite.model.Quiz;
import com.quizwebsite.model.QuizAttempt;
import com.quizwebsite.model.User;
import com.quizwebsite.util.DBConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {

	private UserDAO dao;
	private QuizDAO quizDAO;
	private QuizAttemptDAO attemptDAO;

	@BeforeEach
	public void setUp() throws SQLException {
		dao = new UserDAO();
		quizDAO = new QuizDAO();
		attemptDAO = new QuizAttemptDAO();
		try (Connection conn = DBConnection.get();
				 Statement st = conn.createStatement()) {
			st.executeUpdate("DELETE FROM users");
		}
	}

	@Test
	public void testInsertReturnsGeneratedId() throws SQLException {
		long id = dao.insert(new User("luka", "hash", "salt"));
		assertTrue(id > 0);
	}

	@Test
	public void testFindById() throws SQLException {
		long id = dao.insert(new User("luka", "hash", "salt"));
		User found = dao.findById(id);
		assertNotNull(found);
		assertEquals("luka", found.getUsername());
		assertEquals("hash", found.getPasswordHash());
		assertEquals("salt", found.getSalt());
		assertFalse(found.isAdmin());
	}

	@Test
	public void testFindByIdNotFound() throws SQLException {
		assertNull(dao.findById(99999));
	}

	@Test
	public void testFindByUsername() throws SQLException {
		dao.insert(new User("luka", "hash", "salt"));
		User found = dao.findByUsername("luka");
		assertNotNull(found);
		assertEquals("luka", found.getUsername());
	}

	@Test
	public void testFindByUsernameNotFound() throws SQLException {
		assertNull(dao.findByUsername("ghost"));
	}

	@Test
	public void testInsertedUserDefaults() throws SQLException {
		long id = dao.insert(new User("luka", "hash", "salt"));
		User found = dao.findById(id);
		assertFalse(found.isAdmin()); // is_admin defaults false
		assertNotNull(found.getCreatedAt()); // created_at auto-set
	}

	@Test
	public void testUpdateAdminStatus() throws SQLException {
		long id = dao.insert(new User("luka", "hash", "salt"));
		dao.updateAdminStatus(id, true);
		assertTrue(dao.findById(id).isAdmin());

		dao.updateAdminStatus(id, false);
		assertFalse(dao.findById(id).isAdmin());
	}

	@Test
	public void testDelete() throws SQLException {
		long id = dao.insert(new User("luka", "hash", "salt"));
		dao.delete(id);
		assertNull(dao.findById(id));
	}

	@Test
	public void testCountAll() throws SQLException {
		assertEquals(0, dao.countAll());
		dao.insert(new User("luka", "hash", "salt"));
		dao.insert(new User("nick", "hash", "salt"));
		assertEquals(2, dao.countAll());
	}

	@Test
	public void testSearchByUsernamePartialMatch() throws SQLException {
		dao.insert(new User("luka", "hash", "salt"));
		dao.insert(new User("lukas", "hash", "salt"));
		dao.insert(new User("ilia", "hash", "salt"));

		List<User> results = dao.searchByUsername("luk");
		assertEquals(2, results.size());
	}

	@Test
	public void testSearchByUsernameNoMatch() throws SQLException {
		dao.insert(new User("luka", "hash", "salt"));
		List<User> results = dao.searchByUsername("xyz");
		assertTrue(results.isEmpty());
	}

	@Test
	public void testUsernameUniqueConstraint() throws SQLException {
		dao.insert(new User("luka", "hash", "salt"));
		assertThrows(SQLException.class,
						() -> dao.insert(new User("luka", "hash2", "salt2")));
	}

	@Test
	public void testFindUsersWithStatsCounts() throws SQLException {
		long id = dao.insert(new User("luka", "hash", "salt"));
		long quizId = insertQuiz(id, "Quiz A");
		insertAttempt(id, quizId, false);
		insertAttempt(id, quizId, true); // practice excluded

		UserDAO.UserStats found = dao.findUsersWithStats(null).get(0);
		assertEquals("luka", found.username());
		assertEquals(1, found.quizzesCreated());
		assertEquals(1, found.quizzesTaken());
	}

	@Test
	public void testFindUsersWithStatsFiltersByUsername() throws SQLException {
		dao.insert(new User("luka", "hash", "salt"));
		dao.insert(new User("ilia", "hash", "salt"));

		List<UserDAO.UserStats> stats = dao.findUsersWithStats("luk");
		assertEquals(1, stats.size());
		assertEquals("luka", stats.get(0).username());
	}

	@Test
	public void testFindUsersWithStatsNoActivity() throws SQLException {
		dao.insert(new User("luka", "hash", "salt"));

		UserDAO.UserStats found = dao.findUsersWithStats(null).get(0);
		assertEquals(0, found.quizzesCreated());
		assertEquals(0, found.quizzesTaken());
	}

	@Test
	public void testFindTopCreatorsRanksByQuizCount() throws SQLException {
		long prolific = dao.insert(new User("prolific", "hash", "salt"));
		long casual = dao.insert(new User("casual", "hash", "salt"));
		insertQuiz(prolific, "Quiz A");
		insertQuiz(prolific, "Quiz B");
		insertQuiz(casual, "Quiz C");

		List<UserDAO.UserStats> top = dao.findTopCreators(10);
		assertEquals(2, top.size());
		assertEquals("prolific", top.get(0).username());
		assertEquals(2, top.get(0).quizzesCreated());
	}

	@Test
	public void testFindTopCreatorsRespectsLimit() throws SQLException {
		for (int i = 0; i < 3; i++) {
			insertQuiz(dao.insert(new User("creator" + i, "hash", "salt")), "Quiz " + i);
		}
		assertEquals(2, dao.findTopCreators(2).size());
	}

	@Test
	public void testFindTopCreatorsExcludesUsersWithNoQuizzes() throws SQLException {
		dao.insert(new User("noquizzes", "hash", "salt"));
		assertTrue(dao.findTopCreators(10).isEmpty());
	}

	@Test
	public void testFindTopTakersRanksByAttemptCount() throws SQLException {
		long active = dao.insert(new User("active", "hash", "salt"));
		long quizId = insertQuiz(active, "Quiz A");
		insertAttempt(active, quizId, false);
		insertAttempt(active, quizId, false);
		insertAttempt(active, quizId, true); // practice excluded

		List<UserDAO.UserStats> top = dao.findTopTakers(10);
		assertEquals(1, top.size());
		assertEquals("active", top.get(0).username());
		assertEquals(2, top.get(0).quizzesTaken());
	}

	@Test
	public void testFindTopTakersExcludesUsersWithNoAttempts() throws SQLException {
		dao.insert(new User("noattempts", "hash", "salt"));
		assertTrue(dao.findTopTakers(10).isEmpty());
	}

	private long insertQuiz(long creatorId, String title) throws SQLException {
		return quizDAO.insert(new Quiz(creatorId, title, null, false, true, false, false));
	}

	private void insertAttempt(long userId, long quizId, boolean practice) throws SQLException {
		attemptDAO.insert(new QuizAttempt(userId, quizId, 5, 10, 60, practice));
	}
}