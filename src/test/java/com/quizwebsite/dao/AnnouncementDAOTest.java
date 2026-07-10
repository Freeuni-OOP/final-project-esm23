package com.quizwebsite.dao;

import com.quizwebsite.model.User;
import com.quizwebsite.util.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AnnouncementDAOTest {

	private AnnouncementDAO dao;
	private UserDAO userDao;
	private long adminId;

	@BeforeEach
	public void setUp() throws Exception {
		dao = new AnnouncementDAO();
		userDao = new UserDAO();
		adminId = userDao.insert(new User("admin", "h", "s")); // FK needs a real user
	}

	@AfterEach
	public void tearDown() throws Exception {
		try (Connection conn = DBConnection.get();
				 Statement st = conn.createStatement()) {
			st.execute("DELETE FROM announcements");
			st.execute("DELETE FROM users");
		}
	}

	@Test
	public void testInsertAndFindAll() throws Exception {
		long id = dao.insert(adminId, "server down tonight");
		assertTrue(id > 0);
		List<AnnouncementDAO.Announcement> list = dao.findAll();
		assertEquals(1, list.size());
		AnnouncementDAO.Announcement a = list.get(0);
		assertEquals(adminId, a.adminId());
		assertEquals("server down tonight", a.body());
		assertNotNull(a.createdAt());
	}

	@Test
	public void testFindAllNewestFirst() throws Exception {
		dao.insert(adminId, "first");
		dao.insert(adminId, "second");
		List<AnnouncementDAO.Announcement> list = dao.findAll();
		assertEquals("second", list.get(0).body()); // order by created_at DESC
	}

	@Test
	public void testDelete() throws Exception {
		long id = dao.insert(adminId, "temp");
		dao.delete(id);
		assertTrue(dao.findAll().isEmpty());
	}
}