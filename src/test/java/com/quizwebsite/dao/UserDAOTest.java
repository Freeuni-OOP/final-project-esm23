package com.quizwebsite.dao;

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

	@BeforeEach
	public void setUp() throws SQLException {
		dao = new UserDAO();
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
}