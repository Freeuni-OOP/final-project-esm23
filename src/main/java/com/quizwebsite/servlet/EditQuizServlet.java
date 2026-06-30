package com.quizwebsite.servlet;

import com.quizwebsite.dao.*;
import com.quizwebsite.model.*;
import com.quizwebsite.service.QuizService;
import com.quizwebsite.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/edit-quiz")
public class EditQuizServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession().getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        // This is a placeholder. In a real app, you'd load the quiz here.
        // req.setAttribute("quiz", loadedQuiz);
        req.getRequestDispatcher("/WEB-INF/jsp/createQuiz.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String quizIdStr = req.getParameter("quizId");
        if (quizIdStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing quizId");
            return;
        }

        int quizId = Integer.parseInt(quizIdStr);
        String[] types = req.getParameterValues("qType");
        String[] prompts = req.getParameterValues("qPrompt");

        List<Question> questions = new ArrayList<>();
        if (types != null) {
            for (int i = 0; i < types.length; i++) {
                Question q = buildQuestion(req, types[i], prompts[i], i);
                if (q != null) questions.add(q);
            }
        }

        try (Connection conn = DBConnection.get()) {
            QuizDAO quizDAO = new QuizDAO(conn);
            QuestionDAO questionDAO = new QuestionDAO(conn);
            AnswerDAO answerDAO = new AnswerDAO(conn);
            QuestionOptionDAO questionOptionDAO = new QuestionOptionDAO(conn);

            QuizService service = new QuizService(quizDAO, questionDAO, answerDAO, questionOptionDAO);
            service.updateQuizQuestions(quizId, questions);
            resp.sendRedirect(req.getContextPath() + "/quiz?id=" + quizId);
        } catch (Exception e) {
            throw new ServletException("Failed to update quiz", e);
        }
    }

    private Question buildQuestion(HttpServletRequest req, String type, String prompt, int index) {
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
            case "PICTURE_RESPONSE":
                return new PictureResponse(0, 0, prompt, req.getParameter("picUrl_" + index), index);
            case "QUESTION_RESPONSE":
                return new QuestionResponse(0, 0, prompt, index);
            default: return null;
        }
    }
}
