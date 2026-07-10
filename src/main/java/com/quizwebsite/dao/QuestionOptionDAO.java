package com.quizwebsite.dao;

import com.quizwebsite.model.QuestionOption;
import com.quizwebsite.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionOptionDAO {

	private QuestionOption mapRow(ResultSet rs) throws SQLException {
		return new QuestionOption(
						rs.getLong("id"),
						rs.getLong("question_id"),
						rs.getString("option_text"),
						rs.getBoolean("is_correct")
		);
	}

	// load all options for a multiple choice question
	public List<QuestionOption> findByQuestion(long questionId) throws SQLException {
		String sql = "SELECT * FROM question_options WHERE question_id = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, questionId);
			try (ResultSet rs = ps.executeQuery()) {
				List<QuestionOption> list = new ArrayList<>();
				while (rs.next()) list.add(mapRow(rs));
				return list;
			}
		}
	}

	public long insert(QuestionOption option) throws SQLException {
		String sql = "INSERT INTO question_options (question_id, option_text, is_correct) VALUES (?, ?, ?)";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, option.getQuestionId());
			ps.setString(2, option.getOptionText());
			ps.setBoolean(3, option.isCorrect());
			ps.executeUpdate();

			try (ResultSet keys = ps.getGeneratedKeys()) {
				keys.next();
				return keys.getLong(1);
			}
		}
	}

	// replace all options for a question (used when editing a quiz)
	public void deleteByQuestion(long questionId) throws SQLException {
		String sql = "DELETE FROM question_options WHERE question_id = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, questionId);
			ps.executeUpdate();
		}
	}
}