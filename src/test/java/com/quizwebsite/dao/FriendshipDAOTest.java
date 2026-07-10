package com.quizwebsite.dao;

import com.quizwebsite.model.Friendship;
import com.quizwebsite.model.FriendshipStatus;
import com.quizwebsite.model.User;
import com.quizwebsite.util.DBConnection;
import org.junit.jupiter.api.*;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

public class FriendshipDAOTest {

	private FriendshipDAO dao;
	private long alice;
	private long bob;

	@BeforeEach
	public void setUp() throws SQLException {
		dao = new FriendshipDAO();
		UserDAO userDao = new UserDAO();
		alice = userDao.insert(new User("alice", "h", "s"));
		bob = userDao.insert(new User("bob", "h", "s"));
	}

	@AfterEach
	public void tearDown() throws SQLException {
		try (Connection c = DBConnection.get();
				 Statement st = c.createStatement()) {
			// cascade clears friendships
			st.executeUpdate("DELETE FROM users");
		}
	}

	@Test
	public void testInsertDefaultsPending() throws SQLException {
		dao.insert(new Friendship(alice, bob));
		assertEquals(FriendshipStatus.PENDING, dao.find(alice, bob).getStatus());
	}

	@Test
	public void testAccept() throws SQLException {
		dao.insert(new Friendship(alice, bob));
		dao.accept(alice, bob);
		assertEquals(FriendshipStatus.ACCEPTED, dao.find(alice, bob).getStatus());
	}

	@Test
	public void testDeleteBidirectional() throws SQLException {
		dao.insert(new Friendship(alice, bob));
		dao.delete(bob, alice); // reversed args still removes the row
		assertNull(dao.find(alice, bob));
	}

	@Test
	public void testFindReversed() throws SQLException {
		dao.insert(new Friendship(alice, bob));
		assertNotNull(dao.find(bob, alice)); // arg order doesnt matter
	}

	@Test
	public void testFindAcceptedFiltersStatus() throws SQLException {
		dao.insert(new Friendship(alice, bob));
		assertTrue(dao.findAccepted(alice).isEmpty()); // pending excluded
		dao.accept(alice, bob);
		assertEquals(1, dao.findAccepted(bob).size());
	}

	@Test
	public void testFindPendingReceived() throws SQLException {
		dao.insert(new Friendship(alice, bob));
		assertEquals(1, dao.findPendingReceived(bob).size());
	}

	@Test
	public void testFindPendingSent() throws SQLException {
		dao.insert(new Friendship(alice, bob));
		assertEquals(1, dao.findPendingSent(alice).size());
		assertTrue(dao.findPendingSent(bob).isEmpty()); // bob received it, didn't send it
	}

	@Test
	public void testFindPendingSentExcludesAccepted() throws SQLException {
		dao.insert(new Friendship(alice, bob));
		dao.accept(alice, bob);
		assertTrue(dao.findPendingSent(alice).isEmpty());
	}
}