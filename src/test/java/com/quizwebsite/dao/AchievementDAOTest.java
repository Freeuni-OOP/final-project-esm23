package com.quizwebsite.dao;

import com.quizwebsite.model.User;
import com.quizwebsite.util.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AchievementDAOTest {

	private AchievementDAO dao;
	private UserDAO userDao;
	private long userId;
	private long achId;

	@BeforeEach
	public void setUp() throws Exception {
		dao = new AchievementDAO();
		userDao = new UserDAO();
		userId = userDao.insert(new User("ach_tester", "h", "s"));
		achId = seedAchievement("Amateur Author", "created a quiz");
	}

	@AfterEach
	public void tearDown() throws Exception {
		try (Connection conn = DBConnection.get();
				 Statement st = conn.createStatement()) {
			st.execute("DELETE FROM user_achievements");
			st.execute("DELETE FROM achievements");
			st.execute("DELETE FROM users");
		}
	}

	@Test
	public void testFindAchievements() throws Exception {
		assertEquals("Amateur Author", dao.findByName("Amateur Author").name());
		assertEquals(1, dao.findAll().size());
		assertNull(dao.findByName("nope")); // miss returns null
	}

	@Test
	public void testAwardAndVerify() throws Exception {
		dao.award(userId, achId);
		assertTrue(dao.hasEarned(userId, achId));
		List<AchievementDAO.UserAchievement> earned = dao.findEarnedByUser(userId);
		assertEquals(1, earned.size());
		assertEquals(achId, earned.get(0).achievementId());
		assertNotNull(earned.get(0).earnedAt());
	}

	@Test
	public void testAwardIsIdempotent() throws Exception {
		dao.award(userId, achId);
		dao.award(userId, achId);
		assertEquals(1, dao.findEarnedByUser(userId).size());
	}

	@Test
	public void testHasEarnedFalse() throws Exception {
		assertFalse(dao.hasEarned(userId, achId)); // nothing awarded yet
	}

	// achievements are seeded reference data; insert one directly
	private long seedAchievement(String name, String desc) throws Exception {
		String sql = "INSERT INTO achievements (name, description) VALUES (?, ?)";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, name);
			ps.setString(2, desc);
			ps.executeUpdate();
			try (ResultSet keys = ps.getGeneratedKeys()) {
				keys.next();
				return keys.getLong(1);
			}
		}
	}
}