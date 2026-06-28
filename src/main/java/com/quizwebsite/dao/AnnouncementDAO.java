package com.quizwebsite.dao;

import com.quizwebsite.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// announcement is simple and doesnt need a separate model class;
public class AnnouncementDAO {

	public record Announcement(long id, long adminId, String body, LocalDateTime createdAt) {}

	private Announcement mapRow(ResultSet rs) throws SQLException {
		return new Announcement(
						rs.getLong("id"),
						rs.getLong("admin_id"),
						rs.getString("body"),
						rs.getTimestamp("created_at").toLocalDateTime()
		);
	}

	// homepage shows latest announcements first
	public List<Announcement> findAll() throws SQLException {
		String sql = "SELECT * FROM announcements ORDER BY created_at DESC, id DESC";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql);
				 ResultSet rs = ps.executeQuery()) {
			List<Announcement> list = new ArrayList<>();
			while (rs.next()) list.add(mapRow(rs));
			return list;
		}
	}

	public long insert(long adminId, String body) throws SQLException {
		String sql = "INSERT INTO announcements (admin_id, body) VALUES (?, ?)";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, adminId);
			ps.setString(2, body);
			ps.executeUpdate();
			try (ResultSet keys = ps.getGeneratedKeys()) {
				keys.next();
				return keys.getLong(1);
			}
		}
	}

	public void delete(long id) throws SQLException {
		String sql = "DELETE FROM announcements WHERE id = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			ps.executeUpdate();
		}
	}
}