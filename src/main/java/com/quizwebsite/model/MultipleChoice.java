package com.quizwebsite.model;

import java.util.List;

public class MultipleChoice extends Question {
	private List<QuestionOption> options;

	public MultipleChoice(long id, long quizId, String questionText, int position) {
		super(id, quizId, QuestionType.MULTIPLE_CHOICE, questionText, null, position);
	}

	public List<QuestionOption> getOptions() { return options; }
	public void setOptions(List<QuestionOption> options) { this.options = options; }

	@Override
	public boolean checkAnswer(String userInput, List<Answer> correctAnswers) {
		for (QuestionOption option : options) {
			if (option.isCorrect() && option.getOptionText().equalsIgnoreCase(userInput.trim())) {
				return true;
			}
		}
		return false;
	}
}