package com.quizwebsite.authModule;

import com.quizwebsite.dao.UserDAO;
import com.quizwebsite.model.User;

import java.sql.SQLException;
import java.util.Optional;

// MySQL-backed AuthRepository used by AuthService in production; delegates to UserDAO.
public class JdbcAuthRepository implements AuthRepository {
	private final UserDAO userDAO;

	public JdbcAuthRepository(){
		this(new UserDAO());
	}

	public JdbcAuthRepository(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public boolean existsByUsername(String username) {
		try {
			return userDAO.findByUsername(username) != null;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to check username existence", e);
		}
	}

	@Override
	public Optional<User> findByUsername(String username) {
		try {
			return Optional.ofNullable(userDAO.findByUsername(username));
		} catch (SQLException e) {
			throw new RuntimeException("Failed to find user by username", e);
		}
	}

	@Override
	public void save(User user) {
		try {
			userDAO.insert(user);
		} catch (SQLException e) {
			throw new RuntimeException("Failed to save user", e);
		}
	}

}
