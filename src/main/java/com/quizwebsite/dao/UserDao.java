package com.quizwebsite.dao;

import com.quizwebsite.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
	Optional<User> findById(long id);
	Optional<User> findByUsername(String username);
	List<User> findAll();
	User insert(User user);
	void updateAdminStatus(long userId, boolean isAdmin);
	boolean existsByUsername(String username);
}
