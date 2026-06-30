package com.quizwebsite.dao;

import com.quizwebsite.model.Question;
import java.sql.*;

public class QuestionDAO {
    private final Connection conn;

    public QuestionDAO(Connection conn) {
        this.conn = conn;
    }

    public long insertQuestion(Question q, long quizId) throws SQLException {
        String sql = "INSERT INTO questions (quiz_id, type, prompt, image_url, order_index) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, quizId);
            ps.setString(2, q.getType().name());
            ps.setString(3, q.getQuestionText());
            ps.setString(4, q.getImageUrl());
            ps.setInt(5, q.getPosition());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return -1;
    }
}