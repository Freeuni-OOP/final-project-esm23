package com.quizwebsite.servlet;

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

@WebServlet("/CreateQuizServlet")
public class CreateQuizServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String title       = request.getParameter("title");
        String description = request.getParameter("description");
        boolean randomOrder         = request.getParameter("randomOrder")         != null;
        boolean onePage             = request.getParameter("onePage")             != null;
        boolean immediateCorrection = request.getParameter("immediateCorrection") != null;
        boolean practiceEnabled     = request.getParameter("practiceEnabled")     != null;

        //does validation//
        if (title == null || title.trim().isEmpty()) {
            request.setAttribute("error", "Title cannot be empty.");
            request.setAttribute("description", description);
            request.getRequestDispatcher("createQuiz.jsp").forward(request, response);
            return;
        }

        try {
            QuizDAO quizDAO = new QuizDAO();

            Quiz quiz = new Quiz(
                    0,                    //id — DB will generate//
                    user.getId(),         //creatorId//
                    title.trim(),
                    description,
                    randomOrder,
                    onePage,
                    immediateCorrection,
                    practiceEnabled,
                    null                  //createdAt — DB sets this automatically//
            );

            long quizId = quizDAO.insert(quiz);

            //stores in session so addQuestion.jsp knows which quiz we're building//
            session.setAttribute("newQuizId", quizId);
            session.setAttribute("newQuizTitle", title.trim());
            session.setAttribute("questionPosition", 1); //first question means position 1//

            response.sendRedirect("addQuestion.jsp");

        } catch (SQLException e) {
            throw new ServletException("DB error creating quiz", e);
        }
    }
}