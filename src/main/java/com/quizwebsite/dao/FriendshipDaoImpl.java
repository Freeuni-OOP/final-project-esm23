package com.quizwebsite.dao;

import com.quizwebsite.model.Friendship;
import com.quizwebsite.model.FriendshipStatus;
import com.quizwebsite.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendshipDaoImpl implements FriendshipDao {

	@Override
	public Optional<Friendship> find(long userId, long friendId) {
		String sql = "SELECT * FROM friendships WHERE user_id = ? AND friend_id = ?";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setLong(1, userId);
			ps.setLong(2, friendId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? Optional.of(map(rs)) : Optional.empty();
			}
		} catch (SQLException e) {
			throw new RuntimeException("Failed to find friendship " + userId + " -> " + friendId, e);
		}
	}

	@Override
	public List<Friendship> findAllForUser(long userId) {
		// a friendship row can have the user on either side of the relationship
		String sql = "SELECT * FROM friendships WHERE user_id = ? OR friend_id = ?";
		List<Friendship> friendships = new ArrayList<>();
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setLong(1, userId);
			ps.setLong(2, userId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					friendships.add(map(rs));
				}
			}
			return friendships;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to find friendships for user " + userId, e);
		}
	}

	@Override
	public Friendship insert(Friendship friendship) {
		String sql = "INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, ?)";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setLong(1, friendship.getUserId());
			ps.setLong(2, friendship.getFriendId());
			ps.setString(3, friendship.getStatus().name());
			ps.executeUpdate();
			return find(friendship.getUserId(), friendship.getFriendId()).orElseThrow(() ->
				new IllegalStateException("Inserted friendship could not be re-read"));
		} catch (SQLException e) {
			throw new RuntimeException("Failed to insert friendship", e);
		}
	}

	@Override
	public void updateStatus(long userId, long friendId, FriendshipStatus status) {
		String sql = "UPDATE friendships SET status = ? WHERE user_id = ? AND friend_id = ?";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setString(1, status.name());
			ps.setLong(2, userId);
			ps.setLong(3, friendId);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Failed to update friendship status", e);
		}
	}

	@Override
	public void delete(long userId, long friendId) {
		String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setLong(1, userId);
			ps.setLong(2, friendId);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Failed to delete friendship", e);
		}
	}

	private Friendship map(ResultSet rs) throws SQLException {
		return new Friendship(
			rs.getLong("user_id"),
			rs.getLong("friend_id"),
			FriendshipStatus.valueOf(rs.getString("status")),
			rs.getTimestamp("created_at").toLocalDateTime()
		);
	}
}
