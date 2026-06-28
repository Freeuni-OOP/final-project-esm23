package com.quizwebsite.dao;

import com.quizwebsite.model.MultipleChoice;
import com.quizwebsite.model.Question;
import com.quizwebsite.model.QuestionFactory;
import com.quizwebsite.model.QuestionOption;
import com.quizwebsite.model.QuestionType;
import com.quizwebsite.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles the questions table plus, for MULTIPLE_CHOICE questions, the
 * question_options table. Correct answers for the other question types
 * (FILL_BLANK, QUESTION_RESPONSE, PICTURE_RESPONSE) live in the separate
 * `answers` table and are managed by AnswerDao.
 */
public class QuestionDaoImpl implements QuestionDao {

	@Override
	public Optional<Question> findById(long id) {
		String sql = "SELECT * FROM questions WHERE id = ?";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) return Optional.empty();
				return Optional.of(buildQuestion(c, rs));
			}
		} catch (SQLException e) {
			throw new RuntimeException("Failed to find question by id " + id, e);
		}
	}

	@Override
	public List<Question> findByQuizId(long quizId) {
		String sql = "SELECT * FROM questions WHERE quiz_id = ? ORDER BY position";
		List<Question> questions = new ArrayList<>();
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setLong(1, quizId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					questions.add(buildQuestion(c, rs));
				}
			}
			return questions;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to find questions for quiz " + quizId, e);
		}
	}

	@Override
	public Question insert(Question question) {
		String sql = "INSERT INTO questions (quiz_id, question_type, question_text, image_url, position) " +
			"VALUES (?, ?, ?, ?, ?)";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, question.getQuizId());
			ps.setString(2, question.getType().name());
			ps.setString(3, question.getQuestionText());
			ps.setString(4, question.getImageUrl());
			ps.setInt(5, question.getPosition());
			ps.executeUpdate();

			long id;
			try (ResultSet keys = ps.getGeneratedKeys()) {
				keys.next();
				id = keys.getLong(1);
			}

			Question saved = QuestionFactory.create(
				question.getType(), id, question.getQuizId(),
				question.getQuestionText(), question.getImageUrl(), question.getPosition()
			);

			if (saved instanceof MultipleChoice mc && question instanceof MultipleChoice source
				&& source.getOptions() != null) {
				mc.setOptions(insertOptions(c, id, source.getOptions()));
			}

			return saved;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to insert question for quiz " + question.getQuizId(), e);
		}
	}

	@Override
	public void delete(long id) {
		// question_options and answers cascade-delete via FK ON DELETE CASCADE
		String sql = "DELETE FROM questions WHERE id = ?";
		try (Connection c = DBConnection.get();
			 PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setLong(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Failed to delete question " + id, e);
		}
	}

	private Question buildQuestion(Connection c, ResultSet rs) throws SQLException {
		long id = rs.getLong("id");
		long quizId = rs.getLong("quiz_id");
		QuestionType type = QuestionType.valueOf(rs.getString("question_type"));
		String text = rs.getString("question_text");
		String imageUrl = rs.getString("image_url");
		int position = rs.getInt("position");

		Question question = QuestionFactory.create(type, id, quizId, text, imageUrl, position);

		if (question instanceof MultipleChoice mc) {
			mc.setOptions(findOptions(c, id));
		}

		return question;
	}

	private List<QuestionOption> findOptions(Connection c, long questionId) throws SQLException {
		String sql = "SELECT * FROM question_options WHERE question_id = ? ORDER BY id";
		List<QuestionOption> options = new ArrayList<>();
		try (PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setLong(1, questionId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					options.add(new QuestionOption(
						rs.getLong("id"),
						rs.getLong("question_id"),
						rs.getString("option_text"),
						rs.getBoolean("is_correct")
					));
				}
			}
		}
		return options;
	}

	private List<QuestionOption> insertOptions(Connection c, long questionId, List<QuestionOption> options)
		throws SQLException {
		String sql = "INSERT INTO question_options (question_id, option_text, is_correct) VALUES (?, ?, ?)";
		List<QuestionOption> saved = new ArrayList<>();
		try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			for (QuestionOption option : options) {
				ps.setLong(1, questionId);
				ps.setString(2, option.getOptionText());
				ps.setBoolean(3, option.isCorrect());
				ps.executeUpdate();
				try (ResultSet keys = ps.getGeneratedKeys()) {
					keys.next();
					saved.add(new QuestionOption(keys.getLong(1), questionId, option.getOptionText(), option.isCorrect()));
				}
			}
		}
		return saved;
	}
}
