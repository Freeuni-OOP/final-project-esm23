package com.quizwebsite.servlet;

import com.quizwebsite.dao.*;
import com.quizwebsite.model.*;
import com.quizwebsite.service.AnswerReviewRow;
import com.quizwebsite.service.QuestionOutcome;
import com.quizwebsite.service.QuizScoringService;
import com.quizwebsite.service.ScoringResult;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Handles taking a quiz end to end:
 *   GET  ?quizId=X[&practice=true]   -> starts a new attempt
 *   GET  ?action=next                -> advances to the next question (multi-page mode)
 *   GET  ?action=finish              -> finalizes after the last immediate-correction feedback screen
 *   POST (from the one-page form)    -> grades everything at once
 *   POST (from the single-question form, with a "response" param) -> records one answer
 *
 * Quiz-taking state (current index, accumulated responses, start time) is kept
 * in the HttpSession for the duration of the attempt and cleared once scored.
 */
@WebServlet("/TakeQuizServlet")
public class TakeQuizServlet extends HttpServlet {

    private final QuizDAO quizDAO = new QuizDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final QuestionOptionDAO optionDAO = new QuestionOptionDAO();
    private final AnswerDAO answerDAO = new AnswerDAO();
    private final QuizAttemptDAO attemptDAO = new QuizAttemptDAO();
    private final AnswerAttemptDAO answerAttemptDAO = new AnswerAttemptDAO();
    private final UserDAO userDAO = new UserDAO();

    //delegated service structure responsible for computing point maps and saving attempts to MySQL//
    private final QuizScoringService scoringService =
            new QuizScoringService(questionDAO, answerDAO, attemptDAO, answerAttemptDAO, optionDAO);

    //session Attribute Constant Keys to prevent typos across multi-stage lookups//
    private static final String SESS_QUIZ_ID = "takeQuiz_quizId";
    private static final String SESS_PRACTICE = "takeQuiz_isPractice";
    private static final String SESS_START_TIME = "takeQuiz_startTime";
    private static final String SESS_ORDER = "takeQuiz_questionOrder";
    private static final String SESS_INDEX = "takeQuiz_index";
    private static final String SESS_RESPONSES = "takeQuiz_responses";
    private static final String SESS_QUESTIONS_BY_ID = "takeQuiz_questionsById";


     //orchestrates routing for quiz initialization or page transitions in multi-page sequences//
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        try {
            if ("next".equals(action)) {
                advanceToNextQuestion(request, response, session);
            } else if ("finish".equals(action)) {
                finalizeFromSession(request, response, session, user);
            } else {
                startQuiz(request, response, session);
            }
        } catch (SQLException e) {
            throw new ServletException("Failed while taking quiz", e);
        }
    }

    //receives and processes submitted user form inputs for evaluation//
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Long quizId = (Long) session.getAttribute(SESS_QUIZ_ID);
        if (quizId == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            Quiz quiz = quizDAO.findById(quizId);
            if (quiz == null) {
                response.sendRedirect("index.jsp");
                return;
            }

            //branch logic based on the quiz structure flag configuration//
            if (quiz.isOnePage()) {
                submitOnePage(request, response, session, user, quiz);
            } else {
                submitSingleQuestion(request, response, session, user, quiz);
            }
        } catch (SQLException e) {
            throw new ServletException("Failed while submitting quiz answers", e);
        }
    }


}