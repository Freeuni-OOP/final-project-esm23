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
	public int grade(List<String> responses, List<Answer> correctAnswers) {
		if (responses == null || responses.isEmpty()) return 0;
		String input = responses.get(0).trim();
		for (QuestionOption opt : options) {
			if (opt.isCorrect() && opt.getOptionText().equalsIgnoreCase(input)) return 1;
		}
		return 0;
	}
	@Override
	public int maxPoints(List<Answer> correctAnswers) { return 1; }
}