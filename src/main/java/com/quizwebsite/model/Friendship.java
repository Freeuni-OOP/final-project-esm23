package com.quizwebsite.model;

import java.time.LocalDateTime;

public class Friendship {
	private final long userId;
	private final long friendId;
	private FriendshipStatus status;
	private final LocalDateTime createdAt;

	// For DB reads
	public Friendship(long userId, long friendId, FriendshipStatus status, LocalDateTime createdAt) {
		this.userId = userId;
		this.friendId = friendId;
		this.status = status;
		this.createdAt = createdAt;
	}

	// For new inserts
	public Friendship(long userId, long friendId) {
		this.userId = userId;
		this.friendId = friendId;
		this.status = FriendshipStatus.PENDING;
		this.createdAt = null;
	}

	public long getUserId() { return userId; }
	public long getFriendId() { return friendId; }
	public FriendshipStatus getStatus() { return status; }
	public LocalDateTime getCreatedAt() { return createdAt; }

	public void setStatus(FriendshipStatus status) { this.status = status; }
}