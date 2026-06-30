package com.quizwebsite.servlet;

import com.quizwebsite.dao.QuizDAO;
import com.quizwebsite.dao.QuestionDAO;
import com.quizwebsite.dao.AnswerDAO;
import com.quizwebsite.dao.QuestionOptionDAO;
import com.quizwebsite.model.*;
import com.quizwebsite.service.QuizService;
import com.quizwebsite.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;

@WebServlet("/create-quiz")
public class CreateQuizServlet extends HttpServlet {

    // GET — show blank form
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Auth guard (if not using a filter yet)
        if (req.getSession().getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/jsp/createQuiz.jsp").forward(req, resp);
    }

    // POST — build Quiz object and persist it
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            User creator = (User) session.getAttribute("user");

            // --- 1. Build Quiz shell ---
            Quiz quiz = new Quiz(
                creator.getId(),
                req.getParameter("title"),
                req.getParameter("description"),
                "on".equals(req.getParameter("randomOrder")),
                "on".equals(req.getParameter("singlePage")),
                "on".equals(req.getParameter("immediateCorrection")),
                "on".equals(req.getParameter("practiceAllowed"))
            );

            // --- 2. Parse questions from form ---
            String[] types   = req.getParameterValues("qType");
            String[] prompts = req.getParameterValues("qPrompt");

            List<Question> questions = new ArrayList<>();
            if (types != null) {
                for (int i = 0; i < types.length; i++) {
                    Question q = buildQuestion(req, types[i], prompts[i], i);
                    if (q != null) questions.add(q);
                }
            }
            quiz.setQuestions(questions);

            // --- 3. Persist ---
            try (Connection conn = DBConnection.get()) {
                QuizDAO quizDAO = new QuizDAO(conn);
                QuestionDAO questionDAO = new QuestionDAO(conn);
                AnswerDAO answerDAO = new AnswerDAO(conn);
                QuestionOptionDAO questionOptionDAO = new QuestionOptionDAO(conn);
                
                QuizService service = new QuizService(quizDAO, questionDAO, answerDAO, questionOptionDAO);
                int newId = service.createQuiz(quiz);
                resp.sendRedirect(req.getContextPath() + "/quiz?id=" + newId);
            }

        } catch (Exception e) {
            throw new ServletException("Failed to create quiz", e);
        }
    }

    private Question buildQuestion(HttpServletRequest req,
                                   String type, String prompt, int index) {
        switch (type) {
            case "MULTIPLE_CHOICE": {
                MultipleChoice q = new MultipleChoice(0, 0, prompt, index);
                String[] optionTexts = req.getParameterValues("mcOption_" + index);
                String correctIndexStr = req.getParameter("mcCorrect_" + index);
                int correctIndex = (correctIndexStr != null) ? Integer.parseInt(correctIndexStr) : -1;

                List<QuestionOption> options = new ArrayList<>();
                if (optionTexts != null) {
                    for (int i = 0; i < optionTexts.length; i++) {
                        options.add(new QuestionOption(0, 0, optionTexts[i], i == correctIndex));
                    }
                }
                q.setOptions(options);
                return q;
            }
            case "FILL_BLANK": {
                FillBlank q = new FillBlank(0, 0, prompt, index);
                String[] answerTexts = req.getParameterValues("fibAnswer_" + index);
                List<Answer> answers = new ArrayList<>();
                if (answerTexts != null) {
                    for (String text : answerTexts) {
                        if (text != null && !text.trim().isEmpty()) {
                            answers.add(new Answer(0, 0, text, null));
                        }
                    }
                }
                q.setAnswers(answers);
                return q;
            }
            case "PICTURE_RESPONSE": {
                String url = req.getParameter("picUrl_" + index);
                PictureResponse q = new PictureResponse(0, 0, prompt, url, index);
                return q;
            }
            case "QUESTION_RESPONSE": {
                QuestionResponse q = new QuestionResponse(0, 0, prompt, index);
                return q;
            }
            default: return null;
        }
    }
}