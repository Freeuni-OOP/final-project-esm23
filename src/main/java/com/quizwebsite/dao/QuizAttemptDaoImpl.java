package com.quizwebsite.dao;

import com.quizwebsite.model.QuizAttempt;
import com.quizwebsite.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuizAttemptDaoImpl implements QuizAttemptDao {

	@Override
	public Optional<QuizAttempt> findById(long id) {
		String sql = "SELECT * FROM quiz_attempts WHERE id = ?";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? Optional.of(map(rs)) : Optional.empty();
			}
		} catch (SQLException e) {
			throw new RuntimeException("Failed to find quiz attempt " + id, e);
		}
	}

	@Override
	public List<QuizAttempt> findByUser(long userId) {
		String sql = "SELECT * FROM quiz_attempts WHERE user_id = ? ORDER BY taken_at DESC";
		return queryList(sql, userId);
	}

	@Override
	public List<QuizAttempt> findByQuiz(long quizId) {
		String sql = "SELECT * FROM quiz_attempts WHERE quiz_id = ? ORDER BY taken_at DESC";
		return queryList(sql, quizId);
	}

	private List<QuizAttempt> queryList(String sql, long param) {
		List<QuizAttempt> attempts = new ArrayList<>();
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setLong(1, param);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					attempts.add(map(rs));
				}
			}
			return attempts;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to query quiz attempts", e);
		}
	}

	@Override
	public QuizAttempt insert(QuizAttempt attempt) {
		String sql = "INSERT INTO quiz_attempts (user_id, quiz_id, score, max_score, time_taken_seconds, is_practice) " +
			"VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, attempt.getUserId());
			ps.setLong(2, attempt.getQuizId());
			ps.setInt(3, attempt.getScore());
			ps.setInt(4, attempt.getMaxScore());
			ps.setInt(5, attempt.getTimeTakenSeconds());
			ps.setBoolean(6, attempt.isPractice());
			ps.executeUpdate();

			long id;
			try (ResultSet keys = ps.getGeneratedKeys()) {
				keys.next();
				id = keys.getLong(1);
			}
			return findById(id).orElseThrow(() ->
				new IllegalStateException("Inserted quiz attempt " + id + " could not be re-read"));
		} catch (SQLException e) {
			throw new RuntimeException("Failed to insert quiz attempt", e);
		}
	}

	private QuizAttempt map(ResultSet rs) throws SQLException {
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
}
