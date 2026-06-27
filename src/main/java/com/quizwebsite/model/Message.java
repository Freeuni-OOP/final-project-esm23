package com.quizwebsite.model;

import java.time.LocalDateTime;

public class Message {
	private final long id;
	private final long senderId;
	private final long recipientId;
	private final MessageType type;
	private final String body;
	private final Long quizId;
	private boolean isRead;
	private final LocalDateTime sentAt;

	// For DB reads
	public Message(long id, long senderId, long recipientId, MessageType type, String body, Long quizId, boolean isRead, LocalDateTime sentAt) {
		this.id = id;
		this.senderId = senderId;
		this.recipientId = recipientId;
		this.type = type;
		this.body = body;
		this.quizId = quizId;
		this.isRead = isRead;
		this.sentAt = sentAt;
	}

	// For new inserts
	public Message(long senderId, long recipientId, MessageType type, String body, Long quizId) {
		this.id = 0;
		this.senderId = senderId;
		this.recipientId = recipientId;
		this.type = type;
		this.body = body;
		this.quizId = quizId;
		this.isRead = false;
		this.sentAt = null;
	}

	public long getId() { return id; }
	public long getSenderId() { return senderId; }
	public long getRecipientId() { return recipientId; }
	public MessageType getType() { return type; }
	public String getBody() { return body; }
	public Long getQuizId() { return quizId; }
	public boolean isRead() { return isRead; }
	public LocalDateTime getSentAt() { return sentAt; }

	public void setRead(boolean isRead) { this.isRead = isRead; }
}