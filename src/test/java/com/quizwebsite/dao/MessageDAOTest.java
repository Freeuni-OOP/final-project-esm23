package com.quizwebsite.dao;

import com.quizwebsite.model.Message;
import com.quizwebsite.model.MessageType;
import com.quizwebsite.model.User;
import com.quizwebsite.util.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MessageDAOTest {

	private MessageDAO dao;
	private UserDAO userDao;
	private long senderId;
	private long recipientId;

	@BeforeEach
	public void setUp() throws Exception {
		dao = new MessageDAO();
		userDao = new UserDAO();
		senderId = userDao.insert(new User("sender", "h", "s"));
		recipientId = userDao.insert(new User("recipient", "h", "s"));
	}

	@AfterEach
	public void tearDown() throws Exception {
		try (Connection conn = DBConnection.get();
				 Statement st = conn.createStatement()) {
			st.execute("DELETE FROM messages");
			st.execute("DELETE FROM users");
		}
	}

	@Test
	public void testInsertAndFindById() throws Exception {
		long id = dao.insert(new Message(senderId, recipientId, MessageType.NOTE, "hi", null));
		Message m = dao.findById(id);
		assertEquals("hi", m.getBody());
		assertNull(m.getQuizId());
		assertFalse(m.isRead());
	}

	@Test
	public void testFindByRecipientNewestFirst() throws Exception {
		dao.insert(new Message(senderId, recipientId, MessageType.NOTE, "first", null));
		dao.insert(new Message(senderId, recipientId, MessageType.NOTE, "second", null));
		List<Message> list = dao.findByRecipient(recipientId);
		assertEquals(2, list.size());
		assertEquals("second", list.getFirst().getBody()); // newest first
	}

	@Test
	public void testCountUnread() throws Exception {
		long id = dao.insert(new Message(senderId, recipientId, MessageType.NOTE, "a", null));
		dao.insert(new Message(senderId, recipientId, MessageType.NOTE, "b", null));
		assertEquals(2, dao.countUnread(recipientId));
		dao.markRead(id);
		assertEquals(1, dao.countUnread(recipientId));
	}
}