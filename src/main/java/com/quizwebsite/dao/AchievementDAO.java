package com.quizwebsite.dao;

import com.quizwebsite.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AchievementDAO {

	public record Achievement(long id, String name, String description, String icon) {}
	public record UserAchievement(long userId, long achievementId, LocalDateTime earnedAt) {}

	private Achievement mapAchievement(ResultSet rs) throws SQLException {
		return new Achievement(
						rs.getLong("id"),
						rs.getString("name"),
						rs.getString("description"),
						rs.getString("icon")
		);
	}

	public List<Achievement> findAll() throws SQLException {
		String sql = "SELECT * FROM achievements";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql);
				 ResultSet rs = ps.executeQuery()) {
			List<Achievement> list = new ArrayList<>();
			while (rs.next()) list.add(mapAchievement(rs));
			return list;
		}
	}

	public Achievement findByName(String name) throws SQLException {
		String sql = "SELECT * FROM achievements WHERE name = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, name);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? mapAchievement(rs) : null;
			}
		}
	}

	// all achievements a user has earned (for their profile page)
	public List<UserAchievement> findEarnedByUser(long userId) throws SQLException {
		String sql = "SELECT * FROM user_achievements WHERE user_id = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				List<UserAchievement> list = new ArrayList<>();
				while (rs.next()) {
					list.add(new UserAchievement(
									rs.getLong("user_id"),
									rs.getLong("achievement_id"),
									rs.getTimestamp("earned_at").toLocalDateTime()
					));
				}
				return list;
			}
		}
	}

	// called by AchievementService;
	public void award(long userId, long achievementId) throws SQLException {
		// INSERT IGNORE skips duplicates
		String sql = "INSERT IGNORE INTO user_achievements (user_id, achievement_id) VALUES (?, ?)";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, userId);
			ps.setLong(2, achievementId);
			ps.executeUpdate();
		}
	}

	// check before awarding to avoid redundant DB calls in AchievementService
	public boolean hasEarned(long userId, long achievementId) throws SQLException {
		String sql = "SELECT 1 FROM user_achievements WHERE user_id = ? AND achievement_id = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, userId);
			ps.setLong(2, achievementId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		}
	}
}