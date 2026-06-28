package com.quizwebsite.dao;

import com.quizwebsite.model.Quiz;
import com.quizwebsite.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {

	private Quiz mapRow(ResultSet rs) throws SQLException {
		return new Quiz(
				rs.getLong("id"),
				rs.getLong("creator_id"),
				rs.getString("title"),
				rs.getString("description"),
				rs.getBoolean("random_order"),
				rs.getBoolean("one_page"),
				rs.getBoolean("immediate_correction"),
				rs.getBoolean("practice_enabled"),
				rs.getTimestamp("created_at").toLocalDateTime()
		);
	}

	public Quiz findById(long id) throws SQLException {
		String sql = "SELECT * FROM quizzes WHERE id = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? mapRow(rs) : null;
			}
		}
	}

	// all quizzes made by a specific user for their profile/homepage
	public List<Quiz> findByCreator(long creatorId) throws SQLException {
		String sql = "SELECT * FROM quizzes WHERE creator_id = ? ORDER BY created_at DESC";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, creatorId);
			try (ResultSet rs = ps.executeQuery()) {
				List<Quiz> list = new ArrayList<>();
				while (rs.next()) list.add(mapRow(rs));
				return list;
			}
		}
	}

	// most recently created quizzes for the homepage
	public List<Quiz> findRecent(int limit) throws SQLException {
		String sql = "SELECT * FROM quizzes ORDER BY created_at DESC LIMIT ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, limit);
			try (ResultSet rs = ps.executeQuery()) {
				List<Quiz> list = new ArrayList<>();
				while (rs.next()) list.add(mapRow(rs));
				return list;
			}
		}
	}

	// popular quizzes ranked by total number of attempts
	public List<Quiz> findPopular(int limit) throws SQLException {
		String sql = """
				SELECT q.* FROM quizzes q
				JOIN quiz_attempts a ON a.quiz_id = q.id
				GROUP BY q.id
				ORDER BY COUNT(a.id) DESC
				LIMIT ?
				""";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, limit);
			try (ResultSet rs = ps.executeQuery()) {
				List<Quiz> list = new ArrayList<>();
				while (rs.next()) list.add(mapRow(rs));
				return list;
			}
		}
	}

	// returns the generated id
	public long insert(Quiz quiz) throws SQLException {
		String sql = """
				INSERT INTO quizzes
				(creator_id, title, description, random_order, one_page, immediate_correction, practice_enabled)
				VALUES (?, ?, ?, ?, ?, ?, ?)
				""";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, quiz.getCreatorId());
			ps.setString(2, quiz.getTitle());
			ps.setString(3, quiz.getDescription());
			ps.setBoolean(4, quiz.isRandomOrder());
			ps.setBoolean(5, quiz.isOnePage());
			ps.setBoolean(6, quiz.isImmediateCorrection());
			ps.setBoolean(7, quiz.isPracticeEnabled());
			ps.executeUpdate();

			try (ResultSet keys = ps.getGeneratedKeys()) {
				keys.next();
				return keys.getLong(1);
			}

		}
	}

	public void update(Quiz quiz) throws SQLException {
		String sql = """
				UPDATE quizzes SET title=?, description=?, random_order=?,
				one_page=?, immediate_correction=?, practice_enabled=?
				WHERE id=?
				""";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, quiz.getTitle());
			ps.setString(2, quiz.getDescription());
			ps.setBoolean(3, quiz.isRandomOrder());
			ps.setBoolean(4, quiz.isOnePage());
			ps.setBoolean(5, quiz.isImmediateCorrection());
			ps.setBoolean(6, quiz.isPracticeEnabled());
			ps.setLong(7, quiz.getId());

			ps.executeUpdate();
		}
	}

	// admin or creator can delete; cascade removes questions, attempts, etc.
	public void delete(long quizId) throws SQLException {
		String sql = "DELETE FROM quizzes WHERE id = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, quizId);
			ps.executeUpdate();
		}
	}

	// how many quizzes a user has created (used by AchievementService)
	public int countByCreator(long creatorId) throws SQLException {
		String sql = "SELECT COUNT(*) FROM quizzes WHERE creator_id = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, creatorId);
			try (ResultSet rs = ps.executeQuery()) {
				rs.next();
				return rs.getInt(1);
			}
		}
	}

	// total quizzes on the site, for admin stats
	public int countAll() throws SQLException {
		String sql = "SELECT COUNT(*) FROM quizzes";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql);
				 ResultSet rs = ps.executeQuery()) {
			rs.next();
			return rs.getInt(1);
		}
	}
}