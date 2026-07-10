package com.quizwebsite.util;

import com.quizwebsite.authModule.AuthResult;
import com.quizwebsite.authModule.AuthService;
import com.quizwebsite.dao.UserDAO;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.SQLException;

// creates a default admin account on first startup so reviewers can test admin
// features without registering one and promoting it by hand.
@WebListener
public class AdminSeeder implements ServletContextListener {
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin123";

	private static final int MAX_ATTEMPTS = 5;
	private static final long RETRY_DELAY_MS = 3000;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
			try {
				UserDAO userDAO = new UserDAO();
				if (userDAO.findByUsername(USERNAME) != null) {
					return;
				}
				AuthResult result = new AuthService().register(USERNAME, PASSWORD);
				if (result.isSuccess()) {
					userDAO.updateAdminStatus(result.getUser().getId(), true);
					System.out.println("[AdminSeeder] Created default admin -> username: " + USERNAME + ", password: " + PASSWORD);
				}
				return;
			} catch (SQLException e) {
				if (attempt == MAX_ATTEMPTS) {
					throw new RuntimeException("Failed to seed default admin user after " + MAX_ATTEMPTS + " attempts", e);
				}
				try {
					Thread.sleep(RETRY_DELAY_MS);
				} catch (InterruptedException interrupted) {
					Thread.currentThread().interrupt();
					throw new RuntimeException("Interrupted while retrying admin seed", interrupted);
				}
			}
		}
	}
}
