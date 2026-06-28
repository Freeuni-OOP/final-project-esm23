package com.quizwebsite.dao;

import com.quizwebsite.model.Friendship;
import com.quizwebsite.model.FriendshipStatus;
import com.quizwebsite.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDAO {

	private Friendship mapRow(ResultSet rs) throws SQLException {
		return new Friendship(
						rs.getLong("user_id"),
						rs.getLong("friend_id"),
						FriendshipStatus.valueOf(rs.getString("status")),
						rs.getTimestamp("created_at").toLocalDateTime()
		);
	}

	// send a friend request; status defaults to PENDING in schema
	public void insert(Friendship friendship) throws SQLException {
		String sql = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, friendship.getUserId());
			ps.setLong(2, friendship.getFriendId());
			ps.executeUpdate();
		}
	}

	// accept a pending request;
	// direction (user_id sent, friend_id receives)
	public void accept(long userId, long friendId) throws SQLException {
		String sql = "UPDATE friendships SET status = 'ACCEPTED' WHERE user_id = ? AND friend_id = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, userId);
			ps.setLong(2, friendId);
			ps.executeUpdate();
		}
	}

	// unfriend/reject; remove the row entirely
	public void delete(long userId, long friendId) throws SQLException {
		// remove in both directions in case the row was inserted either way
		String sql = "DELETE FROM friendships WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, userId);
			ps.setLong(2, friendId);
			ps.setLong(3, friendId);
			ps.setLong(4, userId);
			ps.executeUpdate();
		}
	}

	// all confirmed friends for a user (used on profile and homepage feed)
	public List<Friendship> findAccepted(long userId) throws SQLException {
		String sql = """
            SELECT * FROM friendships
            WHERE (user_id = ? OR friend_id = ?) AND status = 'ACCEPTED'
            """;
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, userId);
			ps.setLong(2, userId);
			try (ResultSet rs = ps.executeQuery()) {
				List<Friendship> list = new ArrayList<>();
				while (rs.next()) list.add(mapRow(rs));
				return list;
			}
		}
	}

	// incoming friend requests waiting for this users response
	public List<Friendship> findPendingReceived(long friendId) throws SQLException {
		String sql = "SELECT * FROM friendships WHERE friend_id = ? AND status = 'PENDING'";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, friendId);
			try (ResultSet rs = ps.executeQuery()) {
				List<Friendship> list = new ArrayList<>();
				while (rs.next()) list.add(mapRow(rs));
				return list;
			}
		}
	}

	// check if a friendship (any status) already exists
	public Friendship find(long userId, long friendId) throws SQLException {
		String sql = """
            SELECT * FROM friendships
            WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)
            """;
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, userId);
			ps.setLong(2, friendId);
			ps.setLong(3, friendId);
			ps.setLong(4, userId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? mapRow(rs) : null;
			}
		}
	}
}