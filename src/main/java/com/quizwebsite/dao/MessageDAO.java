package com.quizwebsite.dao;

import com.quizwebsite.model.Message;
import com.quizwebsite.model.MessageType;
import com.quizwebsite.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

	private Message mapRow(ResultSet rs) throws SQLException {
		return new Message(
						rs.getLong("id"),
						rs.getLong("sender_id"),
						rs.getLong("recipient_id"),
						MessageType.valueOf(rs.getString("message_type")),
						rs.getString("body"),
						(Long) rs.getObject("quiz_id"), // null-safe, no wasNull() dance
						rs.getBoolean("is_read"),
						rs.getTimestamp("sent_at").toLocalDateTime()
		);
	}

	public long insert(Message message) throws SQLException {
		String sql = """
				INSERT INTO messages (sender_id, recipient_id, message_type, body, quiz_id)
				VALUES (?, ?, ?, ?, ?)
				""";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, message.getSenderId());
			ps.setLong(2, message.getRecipientId());
			ps.setString(3, message.getType().name());
			ps.setString(4, message.getBody());
			ps.setObject(5, message.getQuizId(), Types.BIGINT);
			ps.executeUpdate();
			try (ResultSet keys = ps.getGeneratedKeys()) {
				keys.next();
				return keys.getLong(1);
			}
		}
	}

	// inbox: all messages for a user, newest first
	public List<Message> findByRecipient(long recipientId) throws SQLException {
		String sql = "SELECT * FROM messages WHERE recipient_id = ? ORDER BY sent_at DESC";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, recipientId);
			try (ResultSet rs = ps.executeQuery()) {
				List<Message> list = new ArrayList<>();
				while (rs.next()) list.add(mapRow(rs));
				return list;
			}
		}
	}

	// unread count for the nav bar badge
	public int countUnread(long recipientId) throws SQLException {
		String sql = "SELECT COUNT(*) FROM messages WHERE recipient_id = ? AND is_read = FALSE";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, recipientId);
			try (ResultSet rs = ps.executeQuery()) {
				rs.next();
				return rs.getInt(1);
			}
		}
	}

	public void markRead(long messageId) throws SQLException {
		String sql = "UPDATE messages SET is_read = TRUE WHERE id = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, messageId);
			ps.executeUpdate();
		}
	}

	public Message findById(long id) throws SQLException {
		String sql = "SELECT * FROM messages WHERE id = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? mapRow(rs) : null;
			}
		}
	}
}