package com.quizwebsite.servlet;

import com.quizwebsite.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// search/browse regular users with their quiz stats, plus a separate admins-only list
@WebServlet("/AdminUsersServlet")
public class AdminUsersServlet extends HttpServlet {

	private final UserDAO userDAO = new UserDAO();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

		String search = request.getParameter("q");
		// lists for filtering
		ArrayList<UserDAO.UserStats> admins = new ArrayList<>();
		ArrayList<UserDAO.UserStats> users = new ArrayList<>();

		try {
			List<UserDAO.UserStats> allUsers = userDAO.findUsersWithStats(search);


			for (UserDAO.UserStats userStats : allUsers) {
				if (userStats.isAdmin()){
					admins.add(userStats);
				}else {
					users.add(userStats);
				}
			}
			// filter out admins and users
			request.setAttribute("users", users);
			request.setAttribute("admins", admins);
			request.setAttribute("searchQuery", search == null ? "" : search);

			request.getRequestDispatcher("/WEB-INF/jsp/adminUsers.jsp")
							.forward(request, response);
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}
}