package com.gvi.project.models.questions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gvi.project.repository.GapFieldEntity;
import com.gvi.project.repository.GapOptionEntity;
import com.gvi.project.repository.McAnswerEntity;
import com.gvi.project.repository.QuestionDataService;
import com.gvi.project.repository.QuestionEntity;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class QuestionProvider implements QuestionService {

	private static final Logger log = LoggerFactory.getLogger(QuestionProvider.class);

	private final ObjectProvider<QuestionDataService> questionDataServiceProvider;
	private volatile List<Question> questions;

	public QuestionProvider(ObjectProvider<QuestionDataService> questionDataServiceProvider) {
		this.questionDataServiceProvider = questionDataServiceProvider;
	}

	private List<Question> loadQuestions() {
		List<Question> current = questions;
		if (current != null) {
			return current;
		}

		synchronized (this) {
			if (questions == null) {
				questions = loadQuestionsPreferDatabase();
			}
			return questions;
		}
	}

	private List<Question> loadQuestionsPreferDatabase() {
		QuestionDataService questionDataService;
		try {
			questionDataService = questionDataServiceProvider.getIfAvailable();
		} catch (BeanCreationException exception) {
			log.warn("QuestionDataService is not available. Falling back to local questions.", exception);
			return List.copyOf(loadQuestionsFromJson());
		}

		if (questionDataService == null) {
			log.warn("No QuestionDataService bean available. Falling back to local questions.");
			return List.copyOf(loadQuestionsFromJson());
		}

		if (!questionDataService.isQuestionSourceAvailable()) {
			log.warn("No configured question data source is available. Falling back to local questions.");
			return List.copyOf(loadQuestionsFromJson());
		}

		try {
			List<Question> databaseQuestions = questionDataService.getAllQuestionsWithDetails().stream()
					.map(this::mapDatabaseQuestion)
					.filter(Objects::nonNull)
					.toList();
			if (!databaseQuestions.isEmpty()) {
				List<Question> shuffled = new ArrayList<>(databaseQuestions);
				Collections.shuffle(shuffled);
				log.info("Loaded {} questions from database.", shuffled.size());
				return List.copyOf(shuffled);
			}
			log.warn("Database query returned 0 questions. Falling back to local questions.");
		} catch (Exception exception) {
			log.warn("Failed to load questions from database. Falling back to local questions.", exception);
		}

		return List.copyOf(loadQuestionsFromJson());
	}

	private List<Question> loadQuestionsFromJson() {
		List<Question> loadedQuestions = new ArrayList<>();
		try {
			InputStream is = getClass().getResourceAsStream("/mock_questions.json");
			if (is == null) {
				log.warn("mock_questions.json not found in resources.");
				return loadedQuestions;
			}

			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(is);

			for (JsonNode node : root) {
				Question q = parseQuestion(node);
				if (q != null) {
					loadedQuestions.add(q);
				}
			}

			Collections.shuffle(loadedQuestions);
			log.info("Loaded {} questions from mock_questions.json.", loadedQuestions.size());
		} catch (Exception e) {
			log.error("Failed to load questions from JSON.", e);
		}

		return loadedQuestions;
	}

	private Question parseQuestion(JsonNode node) {
		String type = node.get("type").asText();
		int id = node.get("id").asInt();
		TopicArea topicArea = TopicArea.valueOf(node.get("topicArea").asText());
		String introText = normalizeQuestionText(node.get("introText").asText());
		String questionText = normalizeQuestionText(node.get("questionText").asText());
		
		// Extract difficulty from JSON if present, otherwise default to MEDIUM
		Difficulty difficulty = node.has("difficulty") 
			? Difficulty.valueOf(node.get("difficulty").asText().toUpperCase(Locale.ROOT))
			: Difficulty.MEDIUM;

		return switch (type) {
			case "MULTIPLE_CHOICE" -> {
				List<Answer> answers = new ArrayList<>();
				for (JsonNode a : node.get("answers")) {
					answers.add(new Answer(normalizeQuestionText(a.get("text").asText()), a.get("points").asInt()));
				}
				boolean allowMultiple = node.has("allowMultipleSelection") && node.get("allowMultipleSelection").asBoolean();
				yield new MultipleChoiceQuestion(id, topicArea, introText, questionText, answers, allowMultiple, difficulty);
			}
			case "TRUE_FALSE" -> {
				boolean correctAnswer = node.get("correctAnswer").asBoolean();
				yield new TrueFalseQuestion(id, topicArea, introText, questionText, correctAnswer, difficulty);
			}
			case "FILL_IN_BLANK" -> {
				List<FillInBlankQuestion.Blank> blanks = new ArrayList<>();
				for (JsonNode b : node.get("blanks")) {
					String textBefore = normalizeQuestionText(b.get("textBefore").asText());
					String textAfter = normalizeQuestionText(b.get("textAfter").asText());
					List<Answer> options = new ArrayList<>();
					for (JsonNode o : b.get("options")) {
						options.add(new Answer(normalizeQuestionText(o.get("text").asText()), o.get("points").asInt()));
					}
					blanks.add(new FillInBlankQuestion.Blank(textBefore, options, textAfter));
				}
				yield new FillInBlankQuestion(id, topicArea, introText, questionText, blanks, difficulty);
			}
			default -> null;
		};
	}

	@Override
	public List<Question> getQuestionsByTopic(TopicArea topicArea) {
		return loadQuestions().stream()
			.filter(q -> q.getTopicArea() == topicArea)
			.collect(Collectors.toList());
	}

	@Override
	public List<Question> getAllQuestions() {
		return new ArrayList<>(loadQuestions());
	}

	private Question mapDatabaseQuestion(QuestionEntity entity) {
		TopicArea topicArea = resolveTopicArea(entity);
		String introText = normalizeQuestionText(topicArea.getDisplayName());
		String questionText = normalizeQuestionText(entity.getStartText());

		return switch (entity.getQuestionType()) {
			case MC -> mapMultipleChoiceQuestion(entity, topicArea, introText, questionText);
			case TF -> mapTrueFalseQuestion(entity, topicArea, introText, questionText);
			case GAP -> mapGapQuestion(entity, topicArea, introText, questionText);
		};
	}

	private Question mapMultipleChoiceQuestion(QuestionEntity entity, TopicArea topicArea, String introText, String questionText) {
		Difficulty difficulty = extractDifficulty(entity);
		
		// Count the number of correct answers first
		long numberOfCorrectAnswers = entity.getMcAnswers().stream()
				.filter(McAnswerEntity::isCorrect)
				.count();
		
		int correctPoints = ScoreCalculator.calculateMultipleChoicePoints(difficulty, (int) numberOfCorrectAnswers, true);
		int wrongPoints = ScoreCalculator.calculateMultipleChoicePoints(difficulty, (int) numberOfCorrectAnswers, false);
		
		List<Answer> answers = entity.getMcAnswers().stream()
				.sorted((left, right) -> Integer.compare(
						valueOrDefault(left.getOptionOrder(), Integer.MAX_VALUE),
						valueOrDefault(right.getOptionOrder(), Integer.MAX_VALUE)
				))
				.map(answer -> new Answer(normalizeQuestionText(answer.getOptionText()), answer.isCorrect() ? correctPoints : wrongPoints))
				.toList();

		if (answers.isEmpty()) {
			return null;
		}

		return new MultipleChoiceQuestion(
				entity.getId(),
				topicArea,
				introText,
				questionText,
				answers,
				Boolean.TRUE.equals(entity.getAllowsMultiple()),
				difficulty
		);
	}

	private Question mapTrueFalseQuestion(QuestionEntity entity, TopicArea topicArea, String introText, String questionText) {
		Difficulty difficulty = extractDifficulty(entity);
		
		boolean correctAnswer = entity.getMcAnswers().stream()
				.sorted((left, right) -> Integer.compare(
						valueOrDefault(left.getOptionOrder(), Integer.MAX_VALUE),
						valueOrDefault(right.getOptionOrder(), Integer.MAX_VALUE)
				))
				.filter(McAnswerEntity::isCorrect)
				.findFirst()
				.map(answer -> {
					String optionText = nullToEmpty(answer.getOptionText()).trim().toLowerCase(Locale.ROOT);
					return optionText.startsWith("wahr") || optionText.startsWith("true");
				})
				.orElse(false);

		return new TrueFalseQuestion(entity.getId(), topicArea, introText, questionText, correctAnswer, difficulty);
	}

	private Question mapGapQuestion(QuestionEntity entity, TopicArea topicArea, String introText, String questionText) {
		Difficulty difficulty = extractDifficulty(entity);
		
		List<GapFieldEntity> gapFieldsList = entity.getGapFields().stream()
				.sorted((left, right) -> Integer.compare(
						valueOrDefault(left.getGapIndex(), Integer.MAX_VALUE),
						valueOrDefault(right.getGapIndex(), Integer.MAX_VALUE)
				))
				.toList();
		
		int totalGaps = gapFieldsList.size();
		
		List<FillInBlankQuestion.Blank> blanks = gapFieldsList.stream()
				.map(gapField -> mapGapBlank(gapField, difficulty, totalGaps))
				.filter(Objects::nonNull)
				.toList();

		if (blanks.isEmpty()) {
			return null;
		}

		return new FillInBlankQuestion(entity.getId(), topicArea, introText, questionText, blanks, difficulty);
	}

	private FillInBlankQuestion.Blank mapGapBlank(GapFieldEntity gapField, Difficulty difficulty, int totalGaps) {
		int correctPoints = ScoreCalculator.calculateGapPoints(difficulty, totalGaps, true);
		int wrongPoints = ScoreCalculator.calculateGapPoints(difficulty, totalGaps, false);
		
		List<Answer> options = gapField.getOptions().stream()
				.sorted((left, right) -> Integer.compare(
						valueOrDefault(left.getOptionOrder(), Integer.MAX_VALUE),
						valueOrDefault(right.getOptionOrder(), Integer.MAX_VALUE)
				))
				.map(option -> new Answer(option.getOptionText(), option.isCorrect() ? correctPoints : wrongPoints))
				.toList();

		if (options.isEmpty()) {
			return null;
		}

		return new FillInBlankQuestion.Blank(
				normalizeQuestionText(gapField.getTextBefore()),
				options,
				normalizeQuestionText(gapField.getTextAfter())
		);
	}



	private TopicArea resolveTopicArea(QuestionEntity entity) {
		return entity.getThemes().stream()
				.map(theme -> theme.getName())
				.map(this::mapTopicArea)
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(TopicArea.SQL_GRUNDLAGEN);
	}

	private TopicArea mapTopicArea(String rawValue) {
		if (rawValue == null || rawValue.isBlank()) {
			return null;
		}

		String normalized = Normalizer.normalize(rawValue, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "")
				.toUpperCase(Locale.ROOT)
				.replace("&", "UND")
				.replaceAll("[^A-Z0-9]+", "_")
				.replaceAll("_+", "_");
		normalized = trimUnderscores(normalized);

		try {
			return TopicArea.valueOf(normalized);
		} catch (IllegalArgumentException ignored) {
			return null;
		}
	}

	private String trimUnderscores(String value) {
		return value.replaceAll("^_+", "").replaceAll("_+$", "");
	}

	/**
	 * Extracts the difficulty level from the question entity's points field.
	 * Maps: 1 -> EASY, 2 -> MEDIUM, 3+ -> HARD
	 */
	private Difficulty extractDifficulty(QuestionEntity entity) {
		int points = valueOrDefault(entity.getPoints(), 1);
		return Difficulty.fromPoints(points);
	}

	private int valueOrDefault(Integer value, int defaultValue) {
		return value == null ? defaultValue : value;
	}

	private String nullToEmpty(String value) {
		return value == null ? "" : value;
	}

	private String normalizeQuestionText(String value) {
		if (value == null || value.isEmpty()) {
			return "";
		}

		String normalizedNewlines = value.replace("\r\n", "\n").replace('\r', '\n');
		String[] lines = normalizedNewlines.split("\n", -1);
		List<String> normalizedLines = new ArrayList<>(lines.length);

		for (String line : lines) {
			normalizedLines.add(line.replaceAll("[ \\t\\f\\x0B]+", " ").trim());
		}

		return String.join("\n", normalizedLines);
	}
}
