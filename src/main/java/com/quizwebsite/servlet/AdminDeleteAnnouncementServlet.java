package com.quizwebsite.servlet;

import com.quizwebsite.dao.AnnouncementDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

// Auth + admin check are handled by AdminFilter
@WebServlet("/AdminDeleteAnnouncementServlet")
public class AdminDeleteAnnouncementServlet extends HttpServlet {

	private AnnouncementDAO announcementDAO = new AnnouncementDAO();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {
		long id = Long.parseLong(request.getParameter("id"));
		try {
			announcementDAO.delete(id);
			response.sendRedirect(request.getContextPath() + "/announcements");
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}
}