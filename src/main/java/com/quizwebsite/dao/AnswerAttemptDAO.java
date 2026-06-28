package com.quizwebsite.dao;

import com.quizwebsite.model.AnswerAttempt;
import com.quizwebsite.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnswerAttemptDAO {

	private AnswerAttempt mapRow(ResultSet rs) throws SQLException {
		return new AnswerAttempt(
						rs.getLong("id"),
						rs.getLong("attempt_id"),
						rs.getLong("question_id"),
						rs.getString("response_text"),
						rs.getBoolean("is_correct")
		);
	}

	public long insert(AnswerAttempt aa) throws SQLException {
		String sql = """
            INSERT INTO attempt_answers (attempt_id, question_id, response_text, is_correct)
            VALUES (?, ?, ?, ?)
            """;
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, aa.getAttemptId());
			ps.setLong(2, aa.getQuestionId());
			ps.setString(3, aa.getResponseText());
			ps.setBoolean(4, aa.isCorrect());
			ps.executeUpdate();
			try (ResultSet keys = ps.getGeneratedKeys()) {
				keys.next();
				return keys.getLong(1);
			}
		}
	}

	// load all per-question responses for a single attempt (results page)
	public List<AnswerAttempt> findByAttempt(long attemptId) throws SQLException {
		String sql = "SELECT * FROM attempt_answers WHERE attempt_id = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, attemptId);
			try (ResultSet rs = ps.executeQuery()) {
				List<AnswerAttempt> list = new ArrayList<>();
				while (rs.next()) list.add(mapRow(rs));
				return list;
			}
		}
	}
}