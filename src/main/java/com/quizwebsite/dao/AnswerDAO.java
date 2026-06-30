package com.quizwebsite.dao;

import com.quizwebsite.model.Answer;
import java.sql.*;

public class AnswerDAO {
    private final Connection conn;

    public AnswerDAO(Connection conn) {
        this.conn = conn;
    }

    public void insertAnswer(Answer answer) throws SQLException {
        String sql = "INSERT INTO answers (question_id, answer_text, slot_index) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, answer.getQuestionId());
            ps.setString(2, answer.getAnswerText());
            if (answer.getSlotIndex() != null) {
                ps.setInt(3, answer.getSlotIndex());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.executeUpdate();
        }
    }
}