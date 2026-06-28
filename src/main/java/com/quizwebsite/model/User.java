package com.quizwebsite.model;

import java.time.LocalDateTime;

public class User {
	private final long id;
	private final String username;
	private final String passwordHash;
	private final String salt;
	private boolean isAdmin;
	private final LocalDateTime createdAt;

	// For DB reads
	public User(long id, String username, String passwordHash, String salt, boolean isAdmin, LocalDateTime createdAt) {
		this.id = id;
		this.username = username;
		this.passwordHash = passwordHash;
		this.salt = salt;
		this.isAdmin = isAdmin;
		this.createdAt = createdAt;
	}

	// For new user inserts
	public User(String username, String passwordHash, String salt) {
		this.id = 0;
		this.username = username;
		this.passwordHash = passwordHash;
		this.salt = salt;
		this.isAdmin = false;
		this.createdAt = null;
	}

	public long getId() { return id; }
	public String getUsername() { return username; }
	public String getPasswordHash() { return passwordHash; }
	public String getSalt() { return salt; }
	public boolean isAdmin() { return isAdmin; }
	public LocalDateTime getCreatedAt() { return createdAt; }

	public void setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }
}