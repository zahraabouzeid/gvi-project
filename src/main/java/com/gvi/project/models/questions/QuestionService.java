package com.gvi.project.models.questions;

import java.util.List;

public interface QuestionService {

	List<Question> getQuestionsByTopic(TopicArea topicArea);

	List<Question> getAllQuestions();

	/**
	 * Get the total count of all questions in the database.
	 * This is used for reward calculation: (correct answers / total questions) * 100
	 * @return total number of questions
	 */
	int getTotalQuestionCount();
}
