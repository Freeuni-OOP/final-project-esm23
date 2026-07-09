package com.quizwebsite.servlet;

import com.quizwebsite.dao.QuizAttemptDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;


@WebServlet("/AdminClearHistoryServlet")
public class AdminClearHistoryServlet extends HttpServlet {

	private final QuizAttemptDAO quizAttemptDAO = new QuizAttemptDAO();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {
		long quizId = Long.parseLong(request.getParameter("quizId"));
		try {
			quizAttemptDAO.deleteByQuiz(quizId);
			response.sendRedirect(request.getContextPath() + "/HomeServlet");
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}
}