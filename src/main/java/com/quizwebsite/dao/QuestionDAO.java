package com.quizwebsite.dao;

import com.quizwebsite.model.*;
import com.quizwebsite.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

	// use the factory so we never new up a subclass here
	private Question mapRow(ResultSet rs) throws SQLException {
		return QuestionFactory.create(
						QuestionType.valueOf(rs.getString("question_type")),
						rs.getLong("id"),
						rs.getLong("quiz_id"),
						rs.getString("question_text"),
						rs.getString("image_url"),
						rs.getInt("position")
		);
	}

	// ordered by position for quizzes that dont shuffle
	public List<Question> findByQuiz(long quizId) throws SQLException {
		String sql = "SELECT * FROM questions WHERE quiz_id = ? ORDER BY position";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, quizId);
			try (ResultSet rs = ps.executeQuery()) {
				List<Question> list = new ArrayList<>();
				while (rs.next()) list.add(mapRow(rs));
				return list;
			}
		}
	}

	public Question findById(long id) throws SQLException {
		String sql = "SELECT * FROM questions WHERE id = ?";
		try (Connection conn = DBConnection.get();
			 	PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? mapRow(rs) : null;
			}
		}
	}

	// returns the generated question id
	public long insert(Question question) throws SQLException {
		String sql = """
            INSERT INTO questions (quiz_id, question_type, question_text, image_url, position)
            VALUES (?, ?, ?, ?, ?)
            """;
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, question.getQuizId());
			ps.setString(2, question.getType().name());
			ps.setString(3, question.getQuestionText());
			ps.setString(4, question.getImageUrl()); // column allows NULL
			ps.setInt(5, question.getPosition());
			ps.executeUpdate();
			try (ResultSet keys = ps.getGeneratedKeys()) {
				keys.next();
				return keys.getLong(1);
			}
		}
	}

	public void delete(long questionId) throws SQLException {
		String sql = "DELETE FROM questions WHERE id = ?";
		try (Connection conn = DBConnection.get();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, questionId);
			ps.executeUpdate();
		}
	}
}