package com.quizwebsite.service;

import com.quizwebsite.dao.QuizDAO;
import com.quizwebsite.dao.QuestionDAO;
import com.quizwebsite.dao.AnswerDAO;
import com.quizwebsite.dao.QuestionOptionDAO;
import com.quizwebsite.model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class QuizService {

    private final QuizDAO quizDAO;
    private final QuestionDAO questionDAO;
    private final AnswerDAO answerDAO;
    private final QuestionOptionDAO questionOptionDAO;

    public QuizService(QuizDAO quizDAO, QuestionDAO questionDAO, AnswerDAO answerDAO, QuestionOptionDAO questionOptionDAO) {
        this.quizDAO = quizDAO;
        this.questionDAO = questionDAO;
        this.answerDAO = answerDAO;
        this.questionOptionDAO = questionOptionDAO;
    }

    /**
     * Saves a quiz shell + all its questions + answers/options atomically.
     * Returns the new quiz id.
     */
    public int createQuiz(Quiz quiz) throws SQLException {
        Connection conn = null;
        try {
            conn = quizDAO.getConnection();
            conn.setAutoCommit(false);

            int quizId = quizDAO.insertQuiz(quiz);
            for (Question q : quiz.getQuestions()) {
                long questionId = questionDAO.insertQuestion(q, quizId);
                saveChildRecords(q, questionId);
            }
            conn.commit();
            return quizId;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    public void updateQuizQuestions(int quizId, List<Question> newQuestions) throws SQLException {
        Connection conn = null;
        try {
            conn = quizDAO.getConnection();
            conn.setAutoCommit(false);
            
            // Delete old questions (cascades or manual deletion of child records)
            // Note: In a real DB, you'd have ON DELETE CASCADE on answers/options.
            quizDAO.deleteQuestions(quizId);
            
            // Insert new ones
            for (Question q : newQuestions) {
                long questionId = questionDAO.insertQuestion(q, quizId);
                saveChildRecords(q, questionId);
            }
            
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    private void saveChildRecords(Question q, long questionId) throws SQLException {
        if (q instanceof MultipleChoice) {
            MultipleChoice mc = (MultipleChoice) q;
            if (mc.getOptions() != null) {
                for (QuestionOption opt : mc.getOptions()) {
                    questionOptionDAO.insertOption(new QuestionOption(0, questionId, opt.getOptionText(), opt.isCorrect()));
                }
            }
        } else if (q instanceof FillBlank) {
            FillBlank fb = (FillBlank) q;
            if (fb.getAnswers() != null) {
                for (Answer ans : fb.getAnswers()) {
                    answerDAO.insertAnswer(new Answer(0, questionId, ans.getAnswerText(), ans.getSlotIndex()));
                }
            }
        }
    }
}
