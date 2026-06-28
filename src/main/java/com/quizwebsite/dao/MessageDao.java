package com.quizwebsite.dao;

import com.quizwebsite.model.Message;

import java.util.List;
import java.util.Optional;

public interface MessageDao {
	Optional<Message> findById(long id);
	List<Message> findByRecipient(long recipientId);
	List<Message> findBySender(long senderId);
	Message insert(Message message);
	void markRead(long messageId);
}
