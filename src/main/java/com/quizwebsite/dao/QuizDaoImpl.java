package com.quizwebsite.dao;

import com.quizwebsite.model.Quiz;
import com.quizwebsite.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuizDaoImpl implements QuizDao {

	@Override
	public Optional<Quiz> findById(long id) {
		String sql = "SELECT * FROM quizzes WHERE id = ?";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? Optional.of(map(rs)) : Optional.empty();
			}
		} catch (SQLException e) {
			throw new RuntimeException("Failed to find quiz by id " + id, e);
		}
	}

	@Override
	public List<Quiz> findByCreator(long creatorId) {
		String sql = "SELECT * FROM quizzes WHERE creator_id = ? ORDER BY created_at DESC";
		List<Quiz> quizzes = new ArrayList<>();
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setLong(1, creatorId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					quizzes.add(map(rs));
				}
			}
			return quizzes;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to find quizzes for creator " + creatorId, e);
		}
	}

	@Override
	public List<Quiz> findAll() {
		String sql = "SELECT * FROM quizzes ORDER BY created_at DESC";
		List<Quiz> quizzes = new ArrayList<>();
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				quizzes.add(map(rs));
			}
			return quizzes;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to find all quizzes", e);
		}
	}

	@Override
	public Quiz insert(Quiz quiz) {
		String sql = "INSERT INTO quizzes (creator_id, title, description, random_order, one_page, " +
			"immediate_correction, practice_enabled) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, quiz.getCreatorId());
			ps.setString(2, quiz.getTitle());
			ps.setString(3, quiz.getDescription());
			ps.setBoolean(4, quiz.isRandomOrder());
			ps.setBoolean(5, quiz.isOnePage());
			ps.setBoolean(6, quiz.isImmediateCorrection());
			ps.setBoolean(7, quiz.isPracticeEnabled());
			ps.executeUpdate();

			long id;
			try (ResultSet keys = ps.getGeneratedKeys()) {
				keys.next();
				id = keys.getLong(1);
			}
			return findById(id).orElseThrow(() ->
				new IllegalStateException("Inserted quiz " + id + " could not be re-read"));
		} catch (SQLException e) {
			throw new RuntimeException("Failed to insert quiz " + quiz.getTitle(), e);
		}
	}

	@Override
	public void update(Quiz quiz) {
		String sql = "UPDATE quizzes SET title = ?, description = ?, random_order = ?, one_page = ?, " +
			"immediate_correction = ?, practice_enabled = ? WHERE id = ?";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setString(1, quiz.getTitle());
			ps.setString(2, quiz.getDescription());
			ps.setBoolean(3, quiz.isRandomOrder());
			ps.setBoolean(4, quiz.isOnePage());
			ps.setBoolean(5, quiz.isImmediateCorrection());
			ps.setBoolean(6, quiz.isPracticeEnabled());
			ps.setLong(7, quiz.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Failed to update quiz " + quiz.getId(), e);
		}
	}

	@Override
	public void delete(long id) {
		String sql = "DELETE FROM quizzes WHERE id = ?";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setLong(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Failed to delete quiz " + id, e);
		}
	}

	private Quiz map(ResultSet rs) throws SQLException {
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
}
