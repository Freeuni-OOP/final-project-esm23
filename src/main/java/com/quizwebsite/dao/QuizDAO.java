package com.quizwebsite.dao;

import com.quizwebsite.model.Quiz;
import java.sql.*;

public class QuizDAO {
    private final Connection conn;

    public QuizDAO(Connection conn) {
        this.conn = conn;
    }

    public Connection getConnection() {
        return conn;
    }

    public int insertQuiz(Quiz quiz) throws SQLException {
        String sql = "INSERT INTO quizzes (creator_id, title, description, random_order, single_page, immediate_correction, practice_allowed, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, quiz.getCreatorId());
            ps.setString(2, quiz.getTitle());
            ps.setString(3, quiz.getDescription());
            ps.setBoolean(4, quiz.isRandomOrder());
            ps.setBoolean(5, quiz.isOnePage());
            ps.setBoolean(6, quiz.isImmediateCorrection());
            ps.setBoolean(7, quiz.isPracticeEnabled());
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public void deleteQuestions(long quizId) throws SQLException {
        String sql = "DELETE FROM questions WHERE quiz_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, quizId);
            ps.executeUpdate();
        }
    }
}