package com.quizwebsite.dao;

import com.quizwebsite.model.Message;
import com.quizwebsite.model.MessageType;
import com.quizwebsite.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageDaoImpl implements MessageDao {

	@Override
	public Optional<Message> findById(long id) {
		String sql = "SELECT * FROM messages WHERE id = ?";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? Optional.of(map(rs)) : Optional.empty();
			}
		} catch (SQLException e) {
			throw new RuntimeException("Failed to find message " + id, e);
		}
	}

	@Override
	public List<Message> findByRecipient(long recipientId) {
		String sql = "SELECT * FROM messages WHERE recipient_id = ? ORDER BY sent_at DESC";
		return queryList(sql, recipientId);
	}

	@Override
	public List<Message> findBySender(long senderId) {
		String sql = "SELECT * FROM messages WHERE sender_id = ? ORDER BY sent_at DESC";
		return queryList(sql, senderId);
	}

	private List<Message> queryList(String sql, long param) {
		List<Message> messages = new ArrayList<>();
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setLong(1, param);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					messages.add(map(rs));
				}
			}
			return messages;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to query messages", e);
		}
	}

	@Override
	public Message insert(Message message) {
		String sql = "INSERT INTO messages (sender_id, recipient_id, message_type, body, quiz_id) " +
			"VALUES (?, ?, ?, ?, ?)";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, message.getSenderId());
			ps.setLong(2, message.getRecipientId());
			ps.setString(3, message.getType().name());
			ps.setString(4, message.getBody());
			if (message.getQuizId() != null) {
				ps.setLong(5, message.getQuizId());
			} else {
				ps.setNull(5, java.sql.Types.BIGINT);
			}
			ps.executeUpdate();

			long id;
			try (ResultSet keys = ps.getGeneratedKeys()) {
				keys.next();
				id = keys.getLong(1);
			}
			return findById(id).orElseThrow(() ->
				new IllegalStateException("Inserted message " + id + " could not be re-read"));
		} catch (SQLException e) {
			throw new RuntimeException("Failed to insert message", e);
		}
	}

	@Override
	public void markRead(long messageId) {
		String sql = "UPDATE messages SET is_read = TRUE WHERE id = ?";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setLong(1, messageId);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Failed to mark message " + messageId + " as read", e);
		}
	}

	private Message map(ResultSet rs) throws SQLException {
		long quizIdRaw = rs.getLong("quiz_id");
		Long quizId = rs.wasNull() ? null : quizIdRaw;
		return new Message(
			rs.getLong("id"),
			rs.getLong("sender_id"),
			rs.getLong("recipient_id"),
			MessageType.valueOf(rs.getString("message_type")),
			rs.getString("body"),
			quizId,
			rs.getBoolean("is_read"),
			rs.getTimestamp("sent_at").toLocalDateTime()
		);
	}
}
