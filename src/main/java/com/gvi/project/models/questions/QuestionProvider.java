package com.gvi.project.models.questions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionProvider implements QuestionService {

	private final List<Question> questions = new ArrayList<>();

	public QuestionProvider() {
		loadQuestionsFromJson();
	}

	private void loadQuestionsFromJson() {
		try {
			InputStream is = getClass().getResourceAsStream("/mock_questions.json");
			if (is == null) {
				System.err.println("mock_questions.json not found in resources!");
				return;
			}

			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(is);

			for (JsonNode node : root) {
				Question q = parseQuestion(node);
				if (q != null) {
					questions.add(q);
				}
			}

			Collections.shuffle(questions);
		} catch (Exception e) {
			System.err.println("Failed to load questions from JSON: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private Question parseQuestion(JsonNode node) {
		String type = node.get("type").asText();
		int id = node.get("id").asInt();
		TopicArea topicArea = TopicArea.valueOf(node.get("topicArea").asText());
		String introText = node.get("introText").asText();
		String questionText = node.get("questionText").asText();

		return switch (type) {
			case "MULTIPLE_CHOICE" -> {
				List<Answer> answers = new ArrayList<>();
				for (JsonNode a : node.get("answers")) {
					answers.add(new Answer(a.get("text").asText(), a.get("points").asInt()));
				}
				boolean allowMultiple = node.has("allowMultipleSelection") && node.get("allowMultipleSelection").asBoolean();
				yield new MultipleChoiceQuestion(id, topicArea, introText, questionText, answers, allowMultiple);
			}
			case "TRUE_FALSE" -> {
				boolean correctAnswer = node.get("correctAnswer").asBoolean();
				yield new TrueFalseQuestion(id, topicArea, introText, questionText, correctAnswer);
			}
			case "FILL_IN_BLANK" -> {
				List<FillInBlankQuestion.Blank> blanks = new ArrayList<>();
				for (JsonNode b : node.get("blanks")) {
					String textBefore = b.get("textBefore").asText();
					String textAfter = b.get("textAfter").asText();
					List<Answer> options = new ArrayList<>();
					for (JsonNode o : b.get("options")) {
						options.add(new Answer(o.get("text").asText(), o.get("points").asInt()));
					}
					blanks.add(new FillInBlankQuestion.Blank(textBefore, options, textAfter));
				}
				yield new FillInBlankQuestion(id, topicArea, introText, questionText, blanks);
			}
			default -> null;
		};
	}

	@Override
	public List<Question> getQuestionsByTopic(TopicArea topicArea) {
		return questions.stream()
			.filter(q -> q.getTopicArea() == topicArea)
			.collect(Collectors.toList());
	}

	@Override
	public List<Question> getAllQuestions() {
		return new ArrayList<>(questions);
	}
}
