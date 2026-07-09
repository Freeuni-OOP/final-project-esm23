package com.quizwebsite.servlet;

import com.quizwebsite.dao.UserDAO;
import com.quizwebsite.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/AdminPromoteUserServlet")
public class AdminPromoteUserServlet extends HttpServlet {

	private final UserDAO userDAO = new UserDAO();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {
		String username = request.getParameter("username");

		try {
			User target = userDAO.findByUsername(username);

			// ignore unknown usernames; promoting an already-admin user does no harm
			if (target != null) {
				userDAO.updateAdminStatus(target.getId(), true);
			}

			response.sendRedirect(request.getContextPath() + "/HomeServlet");
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}
}