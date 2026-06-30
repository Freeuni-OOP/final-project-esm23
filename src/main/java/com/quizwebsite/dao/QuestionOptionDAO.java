package com.quizwebsite.dao;

import com.quizwebsite.model.QuestionOption;
import java.sql.*;

public class QuestionOptionDAO {
    private final Connection conn;

    public QuestionOptionDAO(Connection conn) {
        this.conn = conn;
    }

    public void insertOption(QuestionOption option) throws SQLException {
        String sql = "INSERT INTO question_options (question_id, option_text, is_correct) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, option.getQuestionId());
            ps.setString(2, option.getOptionText());
            ps.setBoolean(3, option.isCorrect());
            ps.executeUpdate();
        }
    }
}