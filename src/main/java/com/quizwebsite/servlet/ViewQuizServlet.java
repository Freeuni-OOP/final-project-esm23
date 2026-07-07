package com.quizwebsite.servlet;

import com.quizwebsite.dao.QuestionDAO;
import com.quizwebsite.dao.QuestionOptionDAO;
import com.quizwebsite.dao.QuizDAO;
import com.quizwebsite.model.MultipleChoice;
import com.quizwebsite.model.Question;
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
import java.util.List;

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
			if(quiz.getCreatorId() != user.getId()) {
				// if the user is NOT the creator, rediret (answer key should be visible only to the creator)
				response.sendRedirect("index.jsp");
				return;
			}

			List<Question> questions = questionDAO.findByQuiz(quizId);
			for (Question question : questions) {
				if (question instanceof MultipleChoice mc) {
					mc.setOptions(optionDAO.findByQuestion(question.getId()));
				}
			}


			request.setAttribute("quiz", quiz);
			request.setAttribute("questions", questions);
			request.getRequestDispatcher("/WEB-INF/jsp/viewQuiz.jsp").forward(request, response);
		}catch (SQLException e) {
		throw new ServletException("Failed while loading quiz", e);
		}
	}
}
