package com.quizwebsite.dao;

import com.quizwebsite.model.Quiz;
import com.quizwebsite.util.DBConnection;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QuizDAOTest {

	private QuizDAO dao;
	private long userId;

	@BeforeEach
	public void setUp() throws SQLException {
		dao = new QuizDAO();
		userId = insertUser("quiz_dao_tester");
	}

	@AfterEach
	public void tearDown() throws SQLException {
		// cascade removes quizzes, attempts, etc.
		deleteUser(userId);
	}

	@Test
	public void testInsertAndFindById() throws SQLException {
		long id = dao.insert(sampleQuiz("History Quiz"));
		Quiz found = dao.findById(id);

		assertNotNull(found);
		assertEquals("History Quiz", found.getTitle());
		assertEquals(userId, found.getCreatorId());
		assertNotNull(found.getCreatedAt()); // db default fills this
	}

	@Test
	public void testFindByIdMissing() throws SQLException {
		assertNull(dao.findById(-1));
	}

	@Test
	public void testFindByCreator() throws SQLException {
		dao.insert(sampleQuiz("Quiz A"));
		dao.insert(sampleQuiz("Quiz B"));

		List<Quiz> list = dao.findByCreator(userId);
		assertEquals(2, list.size());
	}

	@Test
	public void testFindByCreatorOrdersByNewest() throws SQLException {
		long older = dao.insert(sampleQuiz("Older"));
		backdate(older);
		long newer = dao.insert(sampleQuiz("Newer"));

		List<Quiz> list = dao.findByCreator(userId);
		assertEquals(newer, list.get(0).getId()); // newest first
		assertEquals(older, list.get(1).getId());
	}

	@Test
	public void testFindByCreatorEmpty() throws SQLException {
		long other = insertUser("no_quizzes_user");
		try {
			assertTrue(dao.findByCreator(other).isEmpty());
		} finally {
			deleteUser(other);
		}
	}

	@Test
	public void testFindRecentRespectsLimit() throws SQLException {
		dao.insert(sampleQuiz("Q1"));
		dao.insert(sampleQuiz("Q2"));
		dao.insert(sampleQuiz("Q3"));

		// findRecent is global just check the limit is applied
		assertEquals(2, dao.findRecent(2).size());
	}

	@Test
	public void testFindRecentOrdersByNewest() throws SQLException {
		long older = dao.insert(sampleQuiz("Older"));
		backdate(older);
		long newer = dao.insert(sampleQuiz("Newer"));

		List<Quiz> list = dao.findRecent(10);
		assertTrue(indexOf(list, newer) < indexOf(list, older)); // newer appears first
	}

	@Test
	public void testFindPopular() throws SQLException {
		long id = dao.insert(sampleQuiz("Popular Quiz"));
		insertAttempt(id); // needs an attempt to show up

		List<Quiz> list = dao.findPopular(5);
		assertTrue(list.stream().anyMatch(q -> q.getId() == id));
	}

	@Test
	public void testFindPopularRanksByAttemptCount() throws SQLException {
		long less = dao.insert(sampleQuiz("Less Popular"));
		long more = dao.insert(sampleQuiz("More Popular"));

		insertAttempt(less);
		insertAttempt(more);
		insertAttempt(more); // more has two attempts vs one

		List<Quiz> list = dao.findPopular(10);
		assertTrue(indexOf(list, more) < indexOf(list, less)); // more ranks higher
	}

	@Test
	public void testDelete() throws SQLException {
		long id = dao.insert(sampleQuiz("To Delete"));
		dao.delete(id);
		assertNull(dao.findById(id));
	}

	@Test
	public void testDeleteCascadesAttempts() throws SQLException {
		long id = dao.insert(sampleQuiz("With Attempts"));
		insertAttempt(id);

		dao.delete(id);
		assertEquals(0, countAttempts(id)); // cascade cleared the child rows
	}

	@Test
	public void testCountByCreator() throws SQLException {
		assertEquals(0, dao.countByCreator(userId));
		dao.insert(sampleQuiz("Q1"));
		dao.insert(sampleQuiz("Q2"));
		assertEquals(2, dao.countByCreator(userId));
	}

	@Test
	public void testCountAll() throws SQLException {
		int before = dao.countAll();
		dao.insert(sampleQuiz("Q1"));
		assertEquals(before + 1, dao.countAll());
	}

	// helpers

	private Quiz sampleQuiz(String title) {
		return new Quiz(userId, title, "desc", false, true, false, false);
	}

	private int indexOf(List<Quiz> list, long quizId) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId() == quizId) return i;
		}
		return -1;
	}

	private long insertUser(String username) throws SQLException {
		String sql = "INSERT INTO users (username, password_hash, salt) VALUES (?, ?, ?)";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, username);
			ps.setString(2, "hash");
			ps.setString(3, "salt");
			ps.executeUpdate();
			try (ResultSet keys = ps.getGeneratedKeys()) {
				keys.next();
				return keys.getLong(1);
			}
		}
	}

	private void deleteUser(long id) throws SQLException {
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
			ps.setLong(1, id);
			ps.executeUpdate();
		}
	}

	private void insertAttempt(long quizId) throws SQLException {
		String sql = """
				insert into quiz_attempts (user_id, quiz_id, score, max_score, time_taken_seconds)
				values (?, ?, ?, ?, ?)
				""";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, userId);
			ps.setLong(2, quizId);
			ps.setInt(3, 5);
			ps.setInt(4, 10);
			ps.setInt(5, 120);
			ps.executeUpdate();
		}
	}

	private int countAttempts(long quizId) throws SQLException {
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM quiz_attempts WHERE quiz_id = ?")) {
			ps.setLong(1, quizId);
			try (ResultSet rs = ps.executeQuery()) {
				rs.next();
				return rs.getInt(1);
			}
		}
	}


	@Test
	public void testUpdate() throws SQLException {
		long id = dao.insert(sampleQuiz("Old Title"));
		Quiz q = dao.findById(id);

		// flip every field to catch a column-order typo in the sql
		q.setTitle("New Title");
		q.setDescription("New desc");
		q.setRandomOrder(true);
		q.setOnePage(false);
		q.setImmediateCorrection(true);
		q.setPracticeEnabled(true);
		dao.update(q);

		Quiz updated = dao.findById(id);
		assertEquals("New Title", updated.getTitle());
		assertEquals("New desc", updated.getDescription());
		assertTrue(updated.isRandomOrder());
		assertFalse(updated.isOnePage());
		assertTrue(updated.isImmediateCorrection());
		assertTrue(updated.isPracticeEnabled());
	}


	// push a quiz's created_at one hour into the past so ordering tests are deterministic
	private void backdate(long quizId) throws SQLException {
		String sql = "UPDATE quizzes SET created_at = DATE_SUB(NOW(), INTERVAL 1 HOUR) WHERE id = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, quizId);
			ps.executeUpdate();
		}
	}
}