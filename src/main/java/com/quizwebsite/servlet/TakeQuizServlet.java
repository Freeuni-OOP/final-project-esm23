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

    // ---- starting an attempt ----------//


    //sets up active session attributes, processes presentation options (randomization) and serves the initial workspace//
    private void startQuiz(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws SQLException, ServletException, IOException {

        long quizId;
        try {
            quizId = Long.parseLong(request.getParameter("quizId"));
        } catch (NumberFormatException e) {
            response.sendRedirect("index.jsp");
            return;
        }

        Quiz quiz = quizDAO.findById(quizId);
        if (quiz == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        boolean practice = quiz.isPracticeEnabled() && "true".equals(request.getParameter("practice"));

        List<Question> questions = questionDAO.findByQuiz(quizId);
        if (quiz.isRandomOrder()) {
            Collections.shuffle(questions);
        }

        //eagerly stitches layout choices onto polymorphic MultipleChoice definitions before rendering to optimize view generation//
        for (Question q : questions) {
            if (q instanceof MultipleChoice mc) {
                mc.setOptions(optionDAO.findByQuestion(q.getId()));
            }
        }

        //initializes session metrics//
        session.setAttribute(SESS_QUIZ_ID, quizId);
        session.setAttribute(SESS_PRACTICE, practice);
        session.setAttribute(SESS_START_TIME, System.currentTimeMillis());
        session.setAttribute(SESS_RESPONSES, new LinkedHashMap<Long, List<String>>());

        request.setAttribute("quiz", quiz);

        if (quiz.isOnePage()) {
            request.setAttribute("questions", questions);
            request.getRequestDispatcher("/WEB-INF/jsp/takeQuizOnePage.jsp").forward(request, response);
            return;
        }

        //setup indexing pointers required to walk sequentially through multi-page layouts//
        List<Long> order = new ArrayList<>();
        Map<Long, Question> byId = new LinkedHashMap<>();
        for (Question q : questions) {
            order.add(q.getId());
            byId.put(q.getId(), q);
        }
        session.setAttribute(SESS_ORDER, order);
        session.setAttribute(SESS_INDEX, 0);
        session.setAttribute(SESS_QUESTIONS_BY_ID, byId);

        renderQuestionAt(request, response, quiz, order, byId, 0, null);
    }


    // ---- multi-page mode ---------//


    //advances the internal tracking pointer index by 1 and steps the view page forward//
    @SuppressWarnings("unchecked")
    private void advanceToNextQuestion(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws SQLException, ServletException, IOException {

        Long quizId = (Long) session.getAttribute(SESS_QUIZ_ID);
        List<Long> order = (List<Long>) session.getAttribute(SESS_ORDER);
        if (quizId == null || order == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        Quiz quiz = quizDAO.findById(quizId);
        int index = (Integer) session.getAttribute(SESS_INDEX) + 1;

        //catches final boundaries if an application flows past the list limit//
        if (index >= order.size()) {
            finalizeFromSession(request, response, session, (User) session.getAttribute("user"));
            return;
        }

        session.setAttribute(SESS_INDEX, index);
        Map<Long, Question> byId = (Map<Long, Question>) session.getAttribute(SESS_QUESTIONS_BY_ID);
        renderQuestionAt(request, response, quiz, order, byId, index, null);
    }


    //Processes the input for a single question. If Immediate Correction is checked, it computes a row-level evaluation immediately instead of proceeding//
    @SuppressWarnings("unchecked")
    private void submitSingleQuestion(HttpServletRequest request, HttpServletResponse response,
                                      HttpSession session, User user, Quiz quiz) throws SQLException, ServletException, IOException {

        List<Long> order = (List<Long>) session.getAttribute(SESS_ORDER);
        Map<Long, Question> byId = (Map<Long, Question>) session.getAttribute(SESS_QUESTIONS_BY_ID);
        Integer index = (Integer) session.getAttribute(SESS_INDEX);
        if (order == null || byId == null || index == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        long questionId = order.get(index);
        Question question = byId.get(questionId);

        String raw = request.getParameter("response");
        List<String> answer = (raw == null || raw.trim().isEmpty()) ? List.of() : List.of(raw.trim());

        //caches current response intermediate state inside the session maps//
        Map<Long, List<String>> responses = (Map<Long, List<String>>) session.getAttribute(SESS_RESPONSES);
        responses.put(questionId, answer);

        //grades immediately on submission and re-render current index//
        if (quiz.isImmediateCorrection()) {
            List<Answer> correct = answerDAO.findByQuestion(questionId);
            int earned = question.grade(answer, correct);
            int max = question.maxPoints(correct);

            AnswerReviewRow feedback = new AnswerReviewRow(
                    questionId, question.getQuestionText(),
                    answer.isEmpty() ? "(skipped)" : answer.get(0),
                    earned > 0, correctAnswerText(question, correct), earned, max);

            renderQuestionAt(request, response, quiz, order, byId, index, feedback);
            return;
        }

        //standard progression logic when Immediate Correction is turned off//
        if (index == order.size() - 1) {
            finalizeFromSession(request, response, session, user);
        } else {
            session.setAttribute(SESS_INDEX, index + 1);
            renderQuestionAt(request, response, quiz, order, byId, index + 1, null);
        }
    }


    //Central utility used to push localized request variables into the rendering template scope//
    private void renderQuestionAt(HttpServletRequest request, HttpServletResponse response, Quiz quiz,
                                  List<Long> order, Map<Long, Question> byId, int index, AnswerReviewRow feedback)
            throws ServletException, IOException {

        Question question = byId.get(order.get(index));
        request.setAttribute("quiz", quiz);
        request.setAttribute("question", question);
        request.setAttribute("questionNumber", index + 1);
        request.setAttribute("totalQuestions", order.size());
        request.setAttribute("isLastQuestion", index == order.size() - 1);
        request.setAttribute("feedback", feedback); //holds instantaneous flashcard results, if applicable//
        request.getRequestDispatcher("/WEB-INF/jsp/takeQuizQuestion.jsp").forward(request, response);
    }


    // ---- one-page mode ------------//


    //direct execution entrypoint for single-form full quiz dumps//
    private void submitOnePage(HttpServletRequest request, HttpServletResponse response,
                               HttpSession session, User user, Quiz quiz) throws SQLException, ServletException, IOException {

        Map<Long, List<String>> responses = collectOnePageResponses(request);
        finish(request, response, session, user, quiz, responses);
    }


    //scans incoming parameter keys extracting inputs prefix-mapped with 'q_'//
    private Map<Long, List<String>> collectOnePageResponses(HttpServletRequest request) {
        Map<Long, List<String>> responses = new LinkedHashMap<>();
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (!name.startsWith("q_")) continue;
            String value = request.getParameter(name);
            if (value == null || value.trim().isEmpty()) continue;
            try {
                long questionId = Long.parseLong(name.substring(2));
                responses.put(questionId, List.of(value.trim()));
            } catch (NumberFormatException ignored) {
                //avoids processing form control elements that are not questions//
            }
        }
        return responses;
    }


    // ---- shared finalize / scoring --------//


    //wrapper that retrieves data caches from the session scope to hand off to the master finish sequence//
    @SuppressWarnings("unchecked")
    private void finalizeFromSession(HttpServletRequest request, HttpServletResponse response,
                                     HttpSession session, User user) throws SQLException, ServletException, IOException {

        Long quizId = (Long) session.getAttribute(SESS_QUIZ_ID);
        if (quizId == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        Quiz quiz = quizDAO.findById(quizId);
        Map<Long, List<String>> responses = (Map<Long, List<String>>) session.getAttribute(SESS_RESPONSES);
        finish(request, response, session, user, quiz, responses);
    }

    //Computes total time, handles scoring metrics via execution services, gathers layout review historical DTOs, updates leaderboards, and clears state//
    private void finish(HttpServletRequest request, HttpServletResponse response, HttpSession session,
                        User user, Quiz quiz, Map<Long, List<String>> responses) throws SQLException, ServletException, IOException {

        Long startTime = (Long) session.getAttribute(SESS_START_TIME);
        Boolean practice = (Boolean) session.getAttribute(SESS_PRACTICE);
        int timeTakenSeconds = startTime == null ? 0 : (int) ((System.currentTimeMillis() - startTime) / 1000);
        boolean isPractice = practice != null && practice;

        //delegates calculations and transactional insertions down to service layers//
        ScoringResult result = scoringService.score(user.getId(), quiz.getId(), responses, timeTakenSeconds, isPractice);

        //builds un-linked presentation rows for quizResults.jsp to render historical breakdowns efficiently//
        List<Question> questions = questionDAO.findByQuiz(quiz.getId());
        List<AnswerReviewRow> review = new ArrayList<>();
        for (Question q : questions) {
            List<Answer> correct = answerDAO.findByQuestion(q.getId());
            if (q instanceof MultipleChoice mc) {
                mc.setOptions(optionDAO.findByQuestion(q.getId()));
            }
            List<String> userResp = responses.getOrDefault(q.getId(), List.of());
            int earned = earnedFor(result.outcomes(), q.getId());
            int max = q.maxPoints(correct);
            review.add(new AnswerReviewRow(
                    q.getId(), q.getQuestionText(),
                    userResp.isEmpty() ? "(skipped)" : userResp.get(0),
                    earned > 0, correctAnswerText(q, correct), earned, max));
        }

        //pulls current leaderboard data snippets (bypassed entirely for practice modes)//
        List<QuizAttempt> topScores = isPractice ? List.of() : attemptDAO.findTopScores(quiz.getId(), 5);
        Map<Long, String> topScoreNames = new LinkedHashMap<>();
        for (QuizAttempt a : topScores) {
            User scorer = userDAO.findById(a.getUserId());
            topScoreNames.put(a.getId(), scorer != null ? scorer.getUsername() : "unknown");
        }

        //flushes all transactional variables out of the session block to finalize the run loop//
        clearQuizSession(session);

        request.setAttribute("quiz", quiz);
        request.setAttribute("result", result);
        request.setAttribute("review", review);
        request.setAttribute("isPractice", isPractice);
        request.setAttribute("topScores", topScores);
        request.setAttribute("topScoreNames", topScoreNames);
        request.getRequestDispatcher("/WEB-INF/jsp/quizResults.jsp").forward(request, response);
    }


    //flushes state trackers out of memory to close out the session loop safely/
    private void clearQuizSession(HttpSession session) {
        session.removeAttribute(SESS_QUIZ_ID);
        session.removeAttribute(SESS_PRACTICE);
        session.removeAttribute(SESS_START_TIME);
        session.removeAttribute(SESS_ORDER);
        session.removeAttribute(SESS_INDEX);
        session.removeAttribute(SESS_RESPONSES);
        session.removeAttribute(SESS_QUESTIONS_BY_ID);
    }

    private int earnedFor(List<QuestionOutcome> outcomes, long questionId) {
        for (QuestionOutcome o : outcomes) {
            if (o.questionId() == questionId) return o.earned();
        }
        return 0;
    }

    //generates a localized fallback string showing matching answer patterns or selection literals//
    private String correctAnswerText(Question question, List<Answer> correct) {
        if (question instanceof MultipleChoice mc) {
            for (QuestionOption opt : mc.getOptions()) {
                if (opt.isCorrect()) return opt.getOptionText();
            }
            return "(no correct option set)";
        }
        if (correct.isEmpty()) return "(no answer key set)";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < correct.size(); i++) {
            if (i > 0) sb.append(" / ");
            sb.append(correct.get(i).getAnswerText());
        }
        return sb.toString();
    }

}