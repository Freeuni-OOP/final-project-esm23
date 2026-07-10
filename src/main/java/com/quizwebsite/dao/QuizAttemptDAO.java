package com.quizwebsite.dao;

import com.quizwebsite.model.QuizAttempt;
import com.quizwebsite.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizAttemptDAO {

	private QuizAttempt mapRow(ResultSet rs) throws SQLException {
		return new QuizAttempt(
						rs.getLong("id"),
						rs.getLong("user_id"),
						rs.getLong("quiz_id"),
						rs.getInt("score"),
						rs.getInt("max_score"),
						rs.getInt("time_taken_seconds"),
						rs.getBoolean("is_practice"),
						rs.getTimestamp("taken_at").toLocalDateTime()
		);
	}

	public long insert(QuizAttempt attempt) throws SQLException {
		String sql = """
				INSERT INTO quiz_attempts (user_id, quiz_id, score, max_score, time_taken_seconds, is_practice)
				VALUES (?, ?, ?, ?, ?, ?)
				""";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, attempt.getUserId());
			ps.setLong(2, attempt.getQuizId());
			ps.setInt(3, attempt.getScore());
			ps.setInt(4, attempt.getMaxScore());
			ps.setInt(5, attempt.getTimeTakenSeconds());
			ps.setBoolean(6, attempt.isPractice());
			ps.executeUpdate();
			try (ResultSet keys = ps.getGeneratedKeys()) {
				keys.next();
				return keys.getLong(1);
			}
		}
	}

	// user's own history on a specific quiz
	public List<QuizAttempt> findByUserAndQuiz(long userId, long quizId) throws SQLException {
		String sql = "SELECT * FROM quiz_attempts WHERE user_id = ? AND quiz_id = ? ORDER BY taken_at DESC";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, userId);
			ps.setLong(2, quizId);
			try (ResultSet rs = ps.executeQuery()) {
				List<QuizAttempt> list = new ArrayList<>();
				while (rs.next()) list.add(mapRow(rs));
				return list;
			}
		}
	}

	// all attempts by a user
	public List<QuizAttempt> findByUser(long userId) throws SQLException {
		String sql = "SELECT * FROM quiz_attempts WHERE user_id = ? ORDER BY taken_at DESC";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				List<QuizAttempt> list = new ArrayList<>();
				while (rs.next()) list.add(mapRow(rs));
				return list;
			}
		}
	}

	// leaderboard: top scores ranked by score desc, then time asc (spec requirement)
	public List<QuizAttempt> findTopScores(long quizId, int limit) throws SQLException {
		String sql = """
				SELECT * FROM quiz_attempts
				WHERE quiz_id = ? AND is_practice = FALSE
				ORDER BY score DESC, time_taken_seconds ASC
				LIMIT ?
				""";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, quizId);
			ps.setInt(2, limit);
			try (ResultSet rs = ps.executeQuery()) {
				List<QuizAttempt> list = new ArrayList<>();
				while (rs.next()) list.add(mapRow(rs));
				return list;
			}
		}
	}


	// total attempts taken across all quizzes
	public int countAll() throws SQLException {
		String sql = "SELECT COUNT(*) FROM quiz_attempts WHERE is_practice = FALSE";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql);
				 ResultSet rs = ps.executeQuery()) {
			rs.next();
			return rs.getInt(1);
		}
	}
	// admin: wipe all attempt history for a quiz
	public void deleteByQuiz(long quizId) throws SQLException {
		String sql = "DELETE FROM quiz_attempts WHERE quiz_id = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, quizId);
			ps.executeUpdate();
		}
	}
}