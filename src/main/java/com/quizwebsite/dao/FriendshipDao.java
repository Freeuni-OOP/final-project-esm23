package com.quizwebsite.dao;

import com.quizwebsite.model.Friendship;
import com.quizwebsite.model.FriendshipStatus;

import java.util.List;
import java.util.Optional;

public interface FriendshipDao {
	Optional<Friendship> find(long userId, long friendId);
	List<Friendship> findAllForUser(long userId);
	Friendship insert(Friendship friendship);
	void updateStatus(long userId, long friendId, FriendshipStatus status);
	void delete(long userId, long friendId);
}
