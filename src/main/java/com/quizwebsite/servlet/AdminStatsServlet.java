package com.quizwebsite.servlet;

import com.quizwebsite.dao.QuizAttemptDAO;
import com.quizwebsite.dao.QuizDAO;
import com.quizwebsite.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;


@WebServlet("/AdminStatsServlet")
public class AdminStatsServlet extends HttpServlet {

	private static final int DASHBOARD_LIMIT = 5;

	private final UserDAO userDAO = new UserDAO();
	private final QuizDAO quizDAO = new QuizDAO();
	private final QuizAttemptDAO quizAttemptDAO = new QuizAttemptDAO();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

		try {
			request.setAttribute("userCount", userDAO.countAll());
			request.setAttribute("quizCount", quizDAO.countAll());
			// countAll() matches number of quizzes - no practice quizes
			request.setAttribute("attemptCount", quizAttemptDAO.countAll());

			// most active users
			request.setAttribute("topCreators", userDAO.findTopCreators(DASHBOARD_LIMIT));
			request.setAttribute("topTakers", userDAO.findTopTakers(DASHBOARD_LIMIT));

			request.getRequestDispatcher("/WEB-INF/jsp/adminStats.jsp")
							.forward(request, response);
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}
}