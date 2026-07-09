package com.quizwebsite.dao;

import com.quizwebsite.model.User;
import com.quizwebsite.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

	// map a ResultSet row to a User object
	private User mapRow(ResultSet rs) throws SQLException {
		return new User(
				rs.getLong("id"),
				rs.getString("username"),
				rs.getString("password_hash"),
				rs.getString("salt"),
				rs.getBoolean("is_admin"),
				rs.getTimestamp("created_at").toLocalDateTime()
		);
	}

	public User findById(long id) throws SQLException {
		String sql = "SELECT * FROM users WHERE id = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? mapRow(rs) : null;
			}
		}
	}

	public User findByUsername(String username) throws SQLException {
		String sql = "SELECT * FROM users WHERE username = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? mapRow(rs) : null;
			}
		}
	}

	// returns the generated id after insert
	public long insert(User user) throws SQLException {
		String sql = "INSERT INTO users (username, password_hash, salt) VALUES (?, ?, ?)";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getPasswordHash());
			ps.setString(3, user.getSalt());
			ps.executeUpdate();
			try (ResultSet keys = ps.getGeneratedKeys()) {
				keys.next();
				return keys.getLong(1);
			}
		}
	}

	// used by admin to grant or revoke admin rights
	public void updateAdminStatus(long userId, boolean isAdmin) throws SQLException {
		String sql = "UPDATE users SET is_admin = ? WHERE id = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setBoolean(1, isAdmin);
			ps.setLong(2, userId);
			ps.executeUpdate();
		}
	}

	// admin: remove a user and all their data (cascade handles related rows)
	public void delete(long userId) throws SQLException {
		String sql = "DELETE FROM users WHERE id = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, userId);
			ps.executeUpdate();
		}
	}

	// used on the admin stats page
	public int countAll() throws SQLException {
		String sql = "SELECT COUNT(*) FROM users";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql);
				 ResultSet rs = ps.executeQuery()) {
			rs.next();
			return rs.getInt(1);
		}
	}

	// used for friend lookup by username search
	public List<User> searchByUsername(String query) throws SQLException {
		String sql = "SELECT * FROM users WHERE username LIKE ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, "%" + query + "%");
			try (ResultSet rs = ps.executeQuery()) {
				List<User> results = new ArrayList<>();
				while (rs.next()) results.add(mapRow(rs));
				return results;
			}
		}
	}

	// DTO for the admin user dashboard page. User + their activity
	public record UserStats(long id, String username, boolean isAdmin, LocalDateTime createdAt, int quizzesCreated, int quizzesTaken){}

	private UserStats mapStatsRow(ResultSet rs) throws SQLException {
		return new UserStats(
						rs.getLong("id"),
						rs.getString("username"),
						rs.getBoolean("is_admin"),
						rs.getTimestamp("created_at").toLocalDateTime(),
						rs.getInt("quizzes_created"),
						rs.getInt("quizzes_taken")
		);
	}

}