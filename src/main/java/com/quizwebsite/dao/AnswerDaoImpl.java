package com.quizwebsite.dao;

import com.quizwebsite.model.Answer;
import com.quizwebsite.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AnswerDaoImpl implements AnswerDao {

	@Override
	public List<Answer> findByQuestionId(long questionId) {
		String sql = "SELECT * FROM answers WHERE question_id = ? ORDER BY id";
		List<Answer> answers = new ArrayList<>();
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setLong(1, questionId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					answers.add(map(rs));
				}
			}
			return answers;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to find answers for question " + questionId, e);
		}
	}

	@Override
	public Answer insert(Answer answer) {
		String sql = "INSERT INTO answers (question_id, answer_text, slot_index) VALUES (?, ?, ?)";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, answer.getQuestionId());
			ps.setString(2, answer.getAnswerText());
			if (answer.getSlotIndex() != null) {
				ps.setInt(3, answer.getSlotIndex());
			} else {
				ps.setNull(3, Types.INTEGER);
			}
			ps.executeUpdate();

			long id;
			try (ResultSet keys = ps.getGeneratedKeys()) {
				keys.next();
				id = keys.getLong(1);
			}
			return new Answer(id, answer.getQuestionId(), answer.getAnswerText(), answer.getSlotIndex());
		} catch (SQLException e) {
			throw new RuntimeException("Failed to insert answer for question " + answer.getQuestionId(), e);
		}
	}

	@Override
	public void delete(long id) {
		String sql = "DELETE FROM answers WHERE id = ?";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setLong(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Failed to delete answer " + id, e);
		}
	}

	private Answer map(ResultSet rs) throws SQLException {
		int slot = rs.getInt("slot_index");
		Integer slotIndex = rs.wasNull() ? null : slot;
		return new Answer(
			rs.getLong("id"),
			rs.getLong("question_id"),
			rs.getString("answer_text"),
			slotIndex
		);
	}
}
