package com.quizwebsite.dao;

import com.quizwebsite.model.User;
import com.quizwebsite.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

	@Override
	public Optional<User> findById(long id) {
		String sql = "SELECT * FROM users WHERE id = ?";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? Optional.of(map(rs)) : Optional.empty();
			}
		} catch (SQLException e) {
			throw new RuntimeException("Failed to find user by id " + id, e);
		}
	}

	@Override
	public Optional<User> findByUsername(String username) {
		String sql = "SELECT * FROM users WHERE username = ?";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? Optional.of(map(rs)) : Optional.empty();
			}
		} catch (SQLException e) {
			throw new RuntimeException("Failed to find user by username " + username, e);
		}
	}

	@Override
	public List<User> findAll() {
		String sql = "SELECT * FROM users ORDER BY id";
		List<User> users = new ArrayList<>();
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				users.add(map(rs));
			}
			return users;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to find all users", e);
		}
	}

	@Override
	public User insert(User user) {
		String sql = "INSERT INTO users (username, password_hash, salt, is_admin) VALUES (?, ?, ?, ?)";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getPasswordHash());
			ps.setString(3, user.getSalt());
			ps.setBoolean(4, user.isAdmin());
			ps.executeUpdate();

			long id;
			try (ResultSet keys = ps.getGeneratedKeys()) {
				keys.next();
				id = keys.getLong(1);
			}
			return findById(id).orElseThrow(() ->
				new IllegalStateException("Inserted user " + id + " could not be re-read"));
		} catch (SQLException e) {
			throw new RuntimeException("Failed to insert user " + user.getUsername(), e);
		}
	}

	@Override
	public void updateAdminStatus(long userId, boolean isAdmin) {
		String sql = "UPDATE users SET is_admin = ? WHERE id = ?";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setBoolean(1, isAdmin);
			ps.setLong(2, userId);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Failed to update admin status for user " + userId, e);
		}
	}

	@Override
	public boolean existsByUsername(String username) {
		String sql = "SELECT 1 FROM users WHERE username = ?";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			throw new RuntimeException("Failed to check existence for username " + username, e);
		}
	}

	private User map(ResultSet rs) throws SQLException {
		return new User(
			rs.getLong("id"),
			rs.getString("username"),
			rs.getString("password_hash"),
			rs.getString("salt"),
			rs.getBoolean("is_admin"),
			rs.getTimestamp("created_at").toLocalDateTime()
		);
	}
}
