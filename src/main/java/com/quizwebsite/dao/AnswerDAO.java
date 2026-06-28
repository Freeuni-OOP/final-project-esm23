package com.quizwebsite.dao;

import com.quizwebsite.model.Answer;
import com.quizwebsite.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnswerDAO {

	private Answer mapRow(ResultSet rs) throws SQLException {
		int slot = rs.getInt("slot");
		Integer slotIndex = rs.wasNull() ? null : rs.getInt("slot_index");
		return new Answer(
						rs.getLong("id"),
						rs.getLong("question_id"),
						rs.getString("answer_text"),
						slotIndex
		);
	}

	// load all accepted answers for a question (used when scoring)
	public List<Answer> findByQuestion(long questionId) throws SQLException {
		String sql = "SELECT * FROM answers WHERE question_id = ? ORDER BY slot_index";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, questionId);
			try (ResultSet rs = ps.executeQuery()) {
				List<Answer> list = new ArrayList<>();
				while (rs.next()) list.add(mapRow(rs));
				return list;
			}
		}
	}

	public long insert(Answer answer) throws SQLException {
		String sql = "INSERT INTO answers (question_id, answer_text, slot_index) VALUES (?, ?, ?)";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, answer.getQuestionId());
			ps.setString(2, answer.getAnswerText());
			// slot_index is nullable; setNull when not an ordered multi-answer
			if (answer.getSlotIndex() != null) {
				ps.setInt(3, answer.getSlotIndex());
			} else {
				ps.setNull(3, Types.INTEGER);
			}
			ps.executeUpdate();
			try (ResultSet keys = ps.getGeneratedKeys()) {
				keys.next();
				return keys.getLong(1);
			}
		}
	}

	// delete all answers for a question (used when editing quiz questions)
	public void deleteByQuestion(long questionId) throws SQLException {
		String sql = "DELETE FROM answers WHERE question_id = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, questionId);
			ps.executeUpdate();
		}
	}
}