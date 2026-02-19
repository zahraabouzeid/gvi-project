package com.gvi.project.models.questions;

import java.util.List;

public interface QuestionService {

	List<Question> getQuestionsByTopic(TopicArea topicArea);

	List<Question> getAllQuestions();
}
