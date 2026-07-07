package com.quizwebsite.servlet;

import com.quizwebsite.dao.QuestionDAO;
import com.quizwebsite.dao.QuestionOptionDAO;
import com.quizwebsite.dao.QuizDAO;
import com.quizwebsite.model.Quiz;
import com.quizwebsite.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/ViewQuizServlet")
public class ViewQuizServlet extends HttpServlet {

	private final QuizDAO quizDAO = new QuizDAO();
	private final QuestionDAO questionDAO = new QuestionDAO();
	private final QuestionOptionDAO optionDAO = new QuestionOptionDAO();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		long quizId;
		try {
			quizId = Long.parseLong(request.getParameter("quizId"));
		} catch (NumberFormatException e) {
			response.sendRedirect("index.jsp");
			return;
		}

		try {
			Quiz quiz = quizDAO.findById(quizId);
			if(quiz == null) {
				response.sendRedirect("index.jsp");
				return;
			}
			// check ownership

		}catch (SQLException e) {

		}
	}
}
