package com.quizwebsite.servlet;

import com.quizwebsite.dao.QuizDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

// auth & admin check are handled by AdminFilter
@WebServlet("/AdminRemoveQuizServlet")
public class AdminRemoveQuizServlet extends HttpServlet {

	private final QuizDAO quizDAO = new QuizDAO();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

		long quizId = Long.parseLong(request.getParameter("quizId"));

		try {
			quizDAO.delete(quizId);
			response.sendRedirect(request.getContextPath() + "/HomeServlet");
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}
}