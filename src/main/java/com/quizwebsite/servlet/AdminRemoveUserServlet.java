package com.quizwebsite.servlet;

import com.quizwebsite.dao.UserDAO;
import com.quizwebsite.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/AdminRemoveUserServlet")
public class AdminRemoveUserServlet extends HttpServlet {

	private final UserDAO userDAO = new UserDAO();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		User currentAdmin = (User) session.getAttribute("user");
		String username = request.getParameter("username");

		try {
			User target = userDAO.findByUsername(username);

			// ignore unknown usernames and refuse to let an admin delete their own account
			if (target != null && target.getId() != currentAdmin.getId()) {
				userDAO.delete(target.getId());
			}

			response.sendRedirect(request.getContextPath() + "/HomeServlet");
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}
}