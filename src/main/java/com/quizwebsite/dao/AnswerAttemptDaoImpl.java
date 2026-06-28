package com.quizwebsite.dao;

import com.quizwebsite.model.AnswerAttempt;
import com.quizwebsite.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AnswerAttemptDaoImpl implements AnswerAttemptDao {

	@Override
	public List<AnswerAttempt> findByAttemptId(long attemptId) {
		String sql = "SELECT * FROM attempt_answers WHERE attempt_id = ? ORDER BY id";
		List<AnswerAttempt> answers = new ArrayList<>();
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setLong(1, attemptId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					answers.add(map(rs));
				}
			}
			return answers;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to find answer attempts for attempt " + attemptId, e);
		}
	}

	@Override
	public AnswerAttempt insert(AnswerAttempt answerAttempt) {
		String sql = "INSERT INTO attempt_answers (attempt_id, question_id, response_text, is_correct) " +
			"VALUES (?, ?, ?, ?)";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, answerAttempt.getAttemptId());
			ps.setLong(2, answerAttempt.getQuestionId());
			ps.setString(3, answerAttempt.getResponseText());
			ps.setBoolean(4, answerAttempt.isCorrect());
			ps.executeUpdate();

			long id;
			try (ResultSet keys = ps.getGeneratedKeys()) {
				keys.next();
				id = keys.getLong(1);
			}
			return new AnswerAttempt(id, answerAttempt.getAttemptId(), answerAttempt.getQuestionId(),
				answerAttempt.getResponseText(), answerAttempt.isCorrect());
		} catch (SQLException e) {
			throw new RuntimeException("Failed to insert answer attempt", e);
		}
	}

	private AnswerAttempt map(ResultSet rs) throws SQLException {
		return new AnswerAttempt(
			rs.getLong("id"),
			rs.getLong("attempt_id"),
			rs.getLong("question_id"),
			rs.getString("response_text"),
			rs.getBoolean("is_correct")
		);
	}
}
